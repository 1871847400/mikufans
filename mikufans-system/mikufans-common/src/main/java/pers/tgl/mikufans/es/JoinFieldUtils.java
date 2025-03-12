package pers.tgl.mikufans.es;

import cn.hutool.core.lang.Pair;

public class JoinFieldUtils {
    public static Pair<FieldInfo, FieldInfo> findJoinFields(Class<?> curTable, Class<?> joinTable) {
        for (FieldInfo fieldInfo : FieldInfo.getFieldInfos(curTable)) {
            JoinField joinField = fieldInfo.getJoinField();
            if (joinField == null) {
                continue;
            }
            if (joinField.value().equals(joinTable)) {
                for (FieldInfo joinFieldInfo : FieldInfo.getFieldInfos(joinTable)) {
                    if (joinField.joinField().isEmpty() && joinFieldInfo.isKey()) {
                        return new Pair<>(fieldInfo, joinFieldInfo);
                    } else if (joinField.joinField().equals(joinFieldInfo.getProperty())) {
                        return new Pair<>(fieldInfo, joinFieldInfo);
                    }
                }
            }
        }
        return null;
    }
// 使用注解方式处理表连接
//    private void init() {
//        Class<?>[] tableClasses = annotation.tableClass();
//        List<ElasticSyncCache.Table> tables = new ArrayList<>(tableClasses.length);
//        List<ElasticSyncCache.Join> joins = new ArrayList<>(tableClasses.length - 1);
//        for (Class<?> tableClass : tableClasses) {
//            if (!BaseEntity.class.isAssignableFrom(tableClass)) {
//                throw new RuntimeException(tableClass.getName() + " 没有继承BaseEntity");
//            }
//            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
//            if (tableInfo == null) {
//                throw new RuntimeException(tableClass.getName() + " 没有对应的数据库表");
//            }
//            int size = tables.size();
//            tables.add(new ElasticSyncCache.Table(tableInfo, "t" + (size==0?"":size)));
//        }
//        for (int i = 0; i < tables.size() - 1; i++) {
//            ElasticSyncCache.Table cur = tables.get(i);
//            inner: for (int j = i + 1; j < tables.size(); j++) {
//                ElasticSyncCache.Table tar = tables.get(j);
//                for (ElasticSyncCache.Join join : joins) {
//                    //防止重复join,并不是防止重复join同一张表
//                    if (join.getJoinTable().equals(tar)) {
//                        continue inner;
//                    }
//                }
//                Pair<FieldInfo, FieldInfo> pair = JoinFieldUtils.findJoinFields(tar.getTableClass(), cur.getTableClass());
//                if (pair == null) {
//                    pair = JoinFieldUtils.findJoinFields(cur.getTableClass(), tar.getTableClass());
//                }
//                if (pair != null) {
//                    ElasticSyncCache.Join join = new ElasticSyncCache.Join();
//                    join.setJoinTable(tar);
//                    if (pair.getKey().getTable().equals(tar.getTableClass())) {
//                        join.setJoinField(pair.getKey());
//                        join.setThisField(pair.getValue());
//                    } else {
//                        join.setJoinField(pair.getValue());
//                        join.setThisField(pair.getKey());
//                    }
//                    joins.add(join);
//                }
//            }
//        }
//        if (joins.size() != tableClasses.length - 1) {
//            throw new RuntimeException("JOIN次数不正确 " + aClass);
//        }
//    }
}