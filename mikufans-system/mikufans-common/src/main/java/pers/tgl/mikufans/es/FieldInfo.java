package pers.tgl.mikufans.es;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class FieldInfo {
    private Class<?> table;
    private Field field;
    private String column;
    private String property;
    private boolean isKey;

    private FieldInfo() {}

    public JoinField getJoinField() {
        //优先找字段上的注解
        JoinField annotation = AnnotationUtil.getAnnotation(field, JoinField.class);
        //字段上没有,就找getter上的注解
        if (annotation == null) {
            String getter = StrUtil.genGetter(property);
            Method getterMethod = ReflectUtil.getPublicMethod(table, getter);
            if (getterMethod != null) {
                annotation = AnnotationUtil.getAnnotation(getterMethod, JoinField.class);
            }
        }
        return annotation;
    }

    /**
     * 获取表所有字段(包括主键)
     */
    public static List<FieldInfo> getFieldInfos(Class<?> tableClass) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(tableClass);
        if (tableInfo == null) {
            return Collections.emptyList();
        }
        List<FieldInfo> fieldInfos = new ArrayList<>();
        FieldInfo keyFieldInfo = new FieldInfo();
        keyFieldInfo.setTable(tableClass);
        keyFieldInfo.setField(ReflectUtil.getField(tableClass, tableInfo.getKeyProperty()));
        keyFieldInfo.setColumn(tableInfo.getKeyColumn());
        keyFieldInfo.setProperty(tableInfo.getKeyProperty());
        keyFieldInfo.setKey(true);
        fieldInfos.add(keyFieldInfo);
        //getFieldList 不包括主键
        for (TableFieldInfo tableFieldInfo : tableInfo.getFieldList()) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setTable(tableClass);
            fieldInfo.setField(tableFieldInfo.getField());
            fieldInfo.setColumn(tableFieldInfo.getColumn());
            fieldInfo.setProperty(tableFieldInfo.getProperty());
            fieldInfo.setKey(false);
            fieldInfos.add(fieldInfo);
        }
        return fieldInfos;
    }
}