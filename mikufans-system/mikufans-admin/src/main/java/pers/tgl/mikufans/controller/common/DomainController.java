package pers.tgl.mikufans.controller.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 通用控制器,对所有数据库实体的增删改查
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class DomainController extends BaseController {
    private final Validator validator;

    private Object createModel(TableInfo tableInfo, Map<String, Object> params) {
        if (tableInfo == null) {
            throw new CustomException("实体不存在");
        }
        Class<?> modelClass = tableInfo.getEntityType();
        Object model = ReflectUtil.newInstance(modelClass);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            Field field = ReflectUtil.getField(modelClass, e.getKey());
            if (field != null && e.getValue() != null) {
                if (Number.class.isAssignableFrom(field.getType())) {
                    if (!NumberUtil.isNumber(e.getValue().toString())) {
                        continue;
                    }
                }
                ReflectUtil.setFieldValue(model, e.getKey(), e.getValue());
            }
        }
        return model;
    }

    @AppLog("通用实体查询")
    @GetMapping("/admin/model/{model}")
    @PreAuthorize("hasPermission(#model, 'select')")
    public List<?> list(@PathVariable("model") String model, @RequestParam Map<String, Object> params) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(model);
        Object entity = createModel(tableInfo, params);
        return Db.list(Wrappers.query(entity));
    }

    @AppLog("通用实体查询")
    @GetMapping("/admin/model/{model}/page")
    @PreAuthorize("hasPermission(#model, 'select')")
    @SuppressWarnings("all")
    public IPage<?> selectPage(@PathVariable("model") String model, @RequestParam Map<String, Object> params) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(model);
        Object entity = createModel(tableInfo, params);
        IPage<Object> page = createPage();
        String asc = params.getOrDefault("asc", false).toString();
        QueryWrapper<Object> wrapper = new QueryWrapper<>(entity);
//        SqlHelper.execute(tableInfo.getEntityType(), mapper -> {
//            if (mapper instanceof MPJBaseMapper) {
//                MPJBaseMapper mpjBaseMapper = (MPJBaseMapper) mapper;
//                return mpjBaseMapper.selectJoinPage(page, wrapper); //mapper不支持注解查询
//            } else {
//                return mapper.selectPage((IPage) page, (Wrapper) wrapper);
//            }
//        });
        return Db.page(page, wrapper);
    }

    @AppLog("通用实体创建")
    @RepeatSubmit(interval = 250)
    @PreAuthorize("hasPermission(#model, 'create')")
    @PostMapping("/admin/model/{model}")
    public void create(@PathVariable("model") String model, @RequestBody Map<String, Object> body) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(model);
        Object entity = createModel(tableInfo, body);
        Set<ConstraintViolation<Object>> validate = validator.validate(entity, Create.class);
        for (ConstraintViolation<Object> violation : validate) {
            throw new CustomException(violation.getMessage());
        }
        Db.save(entity);
    }

    @AppLog("通用实体更新")
    @PutMapping("/admin/model/{model}")
    @PreAuthorize("hasPermission(#model, 'update')")
    public boolean update(@PathVariable("model") String model, @RequestBody Map<String, Object> body) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(model);
        Object entity = createModel(tableInfo, body);
        //这里使用手动校验id,如果使用注解,每个基类都需要设置
        Object id = ReflectUtil.getFieldValue(entity, tableInfo.getKeyProperty());
        if (id == null) {
            throw new CustomException("缺少目标id");
        }
        Set<ConstraintViolation<Object>> validate = validator.validate(entity, Update.class);
        for (ConstraintViolation<Object> violation : validate) {
            throw new CustomException(violation.getMessage());
        }
        return Db.updateById(entity);
    }

    @AppLog("通用实体删除")
    @DeleteMapping("/admin/model/{model}/{ids}")
    @PreAuthorize("hasPermission(#model, 'remove')")
    public void remove(@PathVariable("model") String model, @PathVariable List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        TableInfo tableInfo = TableInfoHelper.getTableInfo(model);
        if (tableInfo == null) {
            throw new CustomException("实体不存在");
        }
        Db.update(tableInfo.getEntityType()).in(tableInfo.getKeyColumn(), ids).remove();
    }
}