package pers.tgl.mikufans.form;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import pers.tgl.mikufans.domain.system.SysDictData;
import pers.tgl.mikufans.model.Option;
import pers.tgl.mikufans.service.SysDictDataService;

import java.lang.reflect.Field;
import java.util.*;

@Data
@Slf4j
public class AppForm {
    private static volatile Map<String, AppForm> forms = null;

    private final String entityName;
    private final String tableName;
    private final Form form;
    /**
     * 属性 : FormItem
     */
    private final Map<String, Map<String, Object>> formItems;
    /**
     * 专门为下拉框准备的设置项
     */
    private final Map<String, FormSelect> selects;
    /**
     * 为下拉框类型的属性设置下拉列表
     */
    private final Map<String, List<Option>> options;
    /**
     * 为图片组件设置的额外属性
     */
    private final Map<String, FormImage> images;

    private AppForm(Class<?> entityClass) {
        this.entityName = entityClass.getSimpleName();
        this.tableName = StrUtil.toUnderlineCase(entityClass.getSimpleName());
        this.form = entityClass.getAnnotation(Form.class);
        this.formItems = new LinkedHashMap<>();
        this.selects = new LinkedHashMap<>();
        this.options = new LinkedHashMap<>();
        this.images = new LinkedHashMap<>();
        for (Field field : ReflectUtil.getFields(entityClass)) {
            if (field.isAnnotationPresent(FormItem.class)) {
                processItem(field);
                processSelect(field);
                processImage(field);
            }
        }
    }

    private void processItem(Field field) {
        FormItem formItem = AnnotationUtil.getAnnotation(field, FormItem.class);
        Map<String, Object> formItemMap = AnnotationUtil.getAnnotationValueMap(field, FormItem.class);
        String value = formItem.value();
        if (StrUtil.isNotBlank(value)) {
            StandardEvaluationContext context = new StandardEvaluationContext(new FormFunction());
            for (Map.Entry<String, Object> entry : formItemMap.entrySet()) {
                context.setVariable(entry.getKey(), entry.getValue());
            }
            try {
                Object newValue = new SpelExpressionParser().parseExpression(value).getValue(context);
                formItemMap.put("value", newValue);
            } catch (Exception ignored) {}
        }
        String dict = formItem.dict();
        if (StrUtil.isNotBlank(dict)) {
            SysDictDataService dictDataService = SpringUtil.getBean(SysDictDataService.class);
            List<SysDictData> dictData = dictDataService.getDictData(dict);
            List<Option> dictOptions = new ArrayList<>(dictData.size());
            for (SysDictData data : dictData) {
                dictOptions.add(new Option(data.getDictLabel(), data.getDictValue()));
            }
            options.put(field.getName(), dictOptions);
        }
        formItemMap.put("isText", formItem.type().isText());
        formItems.put(field.getName(), formItemMap);
    }

    private void processSelect(Field field) {
        FormSelect formSelect = AnnotationUtil.getAnnotation(field, FormSelect.class);
        if (formSelect == null) {
            return;
        }
        OptionsProvider optionsProvider;
        if (formSelect.provider().isEnum()) {
            optionsProvider = formSelect.provider().getEnumConstants()[0];
        } else {
            optionsProvider = ReflectUtil.newInstance(formSelect.provider());
        }
        options.put(field.getName(), optionsProvider.getOptions());
        selects.put(field.getName(), formSelect);
    }

    private void processImage(Field field) {
        FormImage formImage = AnnotationUtil.getAnnotation(field, FormImage.class);
        if (formImage == null) {
            return;
        }
        images.put(field.getName(), formImage);
    }

    public static AppForm getAppForm(String name) {
        if (forms == null) {
            synchronized (AppForm.class) {
                if (forms == null) {
                    init();
                }
            }
        }
        return forms.get(name);
    }

    /**
     * 部分form可能会操作数据库,需要等tableInfo加载后再运行
     */
    public static void init() {
        Map<String, AppForm> data = new HashMap<>();
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("pers.tgl.mikufans", Form.class);
        for (Class<?> aClass : classes) {
            String key = StrUtil.toUnderlineCase(aClass.getSimpleName());
            data.put(key, new AppForm(aClass));
        }
        forms = data;
    }
}