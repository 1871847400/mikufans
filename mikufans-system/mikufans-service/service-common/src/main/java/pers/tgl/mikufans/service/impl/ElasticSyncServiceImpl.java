package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.common.ElasticSync;
import pers.tgl.mikufans.es.ElasticAutoSync;
import pers.tgl.mikufans.es.FieldInfo;
import pers.tgl.mikufans.mapper.ElasticSyncMapper;
import pers.tgl.mikufans.model.BaseDoc;
import pers.tgl.mikufans.model.ElasticSyncCache;
import pers.tgl.mikufans.service.ElasticSyncService;

import java.lang.reflect.Constructor;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "ES自动同步")
public class ElasticSyncServiceImpl extends BaseServiceImpl<ElasticSync, ElasticSyncMapper> implements ElasticSyncService, ApplicationRunner, SchedulingConfigurer{
    private final ElasticsearchOperations elasticsearchOperations;
    private final List<ElasticSyncCache> caches = new ArrayList<>();

    /**
     * 初始化时机必须在MP加载完所有表后
     */
    private void init() {
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("pers.tgl.mikufans", ElasticAutoSync.class);
        for (Class<?> aClass : classes) {
            Document document = aClass.getAnnotation(Document.class);
            ElasticAutoSync annotation = aClass.getAnnotation(ElasticAutoSync.class);
            List<ElasticSyncCache.Table> tables = CollUtil.newArrayList();
            List<ElasticSyncCache.Join> joins = CollUtil.newArrayList();
            tables.add(new ElasticSyncCache.Table(TableInfoHelper.getTableInfo(annotation.tableClass()), "t"));
            for (ElasticAutoSync.Join join : annotation.joins()) {
                TableInfo tableInfo = TableInfoHelper.getTableInfo(join.joinTable());
                if (tableInfo == null) {
                    throw new RuntimeException("连接表没有对应数据库" + join.joinTable());
                }
                ElasticSyncCache.Table joinTable = new ElasticSyncCache.Table(tableInfo, "t" + tables.size());
                ElasticSyncCache.Join joinInfo = new ElasticSyncCache.Join(join);
                joinInfo.setJoinTable(joinTable);
                for (FieldInfo fieldInfo : FieldInfo.getFieldInfos(join.joinTable())) {
                    if (join.joinField().isEmpty() && fieldInfo.isKey()) {
                        joinInfo.setJoinField(fieldInfo);
                        break;
                    }
                    if (fieldInfo.getProperty().equals(join.joinField())) {
                        joinInfo.setJoinField(fieldInfo);
                        break;
                    }
                }
                Class<?> thisTable = annotation.tableClass();
                if (join.thisTable() != Object.class) {
                    thisTable = join.thisTable();
                }
                for (FieldInfo fieldInfo : FieldInfo.getFieldInfos(thisTable)) {
                    if (join.thisField().isEmpty() && fieldInfo.isKey()) {
                        joinInfo.setThisField(fieldInfo);
                        break;
                    }
                    if (fieldInfo.getProperty().equals(join.thisField())) {
                        joinInfo.setThisField(fieldInfo);
                        break;
                    }
                }
                joins.add(joinInfo);
                tables.add(joinTable);
            }

            ElasticSyncCache cache = new ElasticSyncCache();
            cache.setDocumentClass((Class<? extends BaseDoc>) aClass);
            cache.setAnnotation(annotation);
            cache.setDocument(document);
            cache.setTables(tables);
            cache.setJoins(joins);
            caches.add(cache);
            log.info("文档类 {} 将自动同步到es中,索引名称: {}", aClass.getSimpleName(), document.indexName());
        }
    }

    private void handle(ElasticSyncCache cache) {
        //ES中索引的名称,相当于mysql中的表
        String indexName = cache.getDocument().indexName();
        String taskName = cache.getAnnotation().name();
        ElasticSync elasticSync = wrapper().eq(ElasticSync::getIdxName, indexName)
                .eq(ElasticSync::getTaskName, taskName)
                .one();
        if (elasticSync == null) {
            elasticSync = new ElasticSync();
            elasticSync.setIdxName(indexName);
            elasticSync.setTaskName(taskName);
            elasticSync.setSyncTime(new Date(0));
            Db.save(elasticSync);
        }
        final Date lastTime = elasticSync.getSyncTime();
        final Date now = new Date();
        ElasticSyncCache.Table mainTable = cache.getMainTable();
        //主表查id和deleteFlag
        MPJLambdaWrapper<?> wrapper = new MPJLambdaWrapper<>(mainTable.getTableClass(), mainTable.getAlias())
                .disableLogicDel()
                .disableSubLogicDel()
                .distinct()
                .select(mainTable.getAlias() + "." + mainTable.getKeyColumn(),
                        mainTable.getAlias() + "." + mainTable.getDeleteColumn())
                .gt(mainTable.getAlias() + "." + mainTable.getUpdateColumn(), lastTime);
        //连接子表,不要出现一对多的情况
        //子表只查id
        for (ElasticSyncCache.Join join : cache.getJoins()) {
            String tableName = join.getJoinTable().getTableName();
            String alias = join.getJoinTable().getAlias();
            String thisColumn = cache.getTable(join.getThisField().getTable()).getAlias() + "." + join.getThisField().getColumn();
            String joinColumn = alias + "." + join.getJoinField().getColumn();
            String method = join.getJoin().method();
            wrapper.join(method, true, tableName + " " + alias + " on " + joinColumn + " = " + thisColumn)
                    .select(alias + "." + join.getJoinTable().getKeyColumn() + " as " + alias + "_id")
                    .or().gt(alias + "." + join.getJoinTable().getUpdateColumn(), lastTime);
        }
        //mapList的结果集没有经过类型转换,例如使用typeHandler的字段需要手动转
        List<Map<String, Object>> list = wrapper.mapList();
        List<Object> saveList = new ArrayList<>();
        List<Object> deleteList = new ArrayList<>();
        for (Map<String, Object> entity : list) {
            Object id = entity.get(mainTable.getKeyColumn());
            //如果主表数据被删除了
            Object deleteFlag = entity.get(mainTable.getDeleteColumn());
            if (deleteFlag == null) {
                deleteList.add(id);
            } else {
                Class<?>[] params = new Class<?>[cache.getTables().size()];
                for (int i = 0; i < cache.getTables().size(); i++) {
                    params[i] = cache.getTables().get(i).getTableClass();
                }
                Constructor<? extends BaseDoc> constructor = ReflectUtil.getConstructor(cache.getDocumentClass(), params);
                if (constructor == null) {
                    throw new RuntimeException("没有找到 " + cache.getDocumentClass().getName() +  " 合适的构造方法");
                }
                List<Object> paramObjs = new ArrayList<>(constructor.getParameters().length);
                for (Class<?> tableClass : params) {
                    Object findId = id;
                    if (!tableClass.equals(mainTable.getTableClass())) {
                        findId = entity.get(cache.getTable(tableClass).getAlias() + "_id");
                    }
                    Object param = null;
                    if (findId != null) {
                        param = Db.getById(findId+"", tableClass);
                    }
                    paramObjs.add(param);
                }
                try {
                    saveList.add(constructor.newInstance(paramObjs.toArray()));
                } catch (Exception e) {
                    log.error("构造文档对象失败 docClass={} params={}", cache.getDocumentClass(), JSONUtil.toJsonStr(paramObjs), e);
                    throw new RuntimeException(e);
                }
            }
        }
        if (CollUtil.isNotEmpty(saveList)) {
            elasticsearchOperations.save(saveList);
        }
        for (Object id : deleteList) {
            elasticsearchOperations.delete(id+"", cache.getDocumentClass());
        }
        updateById(elasticSync.getId(), ElasticSync::getSyncTime, now);
        if (saveList.size() + deleteList.size() > 0) {
            String docName = cache.getDocumentClass().getSimpleName();
            log.info("任务: {} 索引: {} 文档: {} 更新: {} 删除: {}",
                    taskName,docName, indexName,
                    saveList.size(), deleteList.size());
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
    }

    /**
     * 每隔一段时间自动同步mysql中的数据到ES中
     * 判断逻辑为行的更新时间>=上次同步时间
     */
    @Override
    public void configureTasks(@NonNull ScheduledTaskRegistrar taskRegistrar) {
        this.init();
        for (ElasticSyncCache cache : this.caches) {
            long fixedDelay = cache.getAnnotation().fixedDelay() * 1000;
            taskRegistrar.addFixedDelayTask(()->this.handle(cache), fixedDelay);
        }
    }
}