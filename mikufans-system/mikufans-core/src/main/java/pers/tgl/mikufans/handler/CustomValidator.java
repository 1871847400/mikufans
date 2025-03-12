package pers.tgl.mikufans.handler;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import pers.tgl.mikufans.validator.ExtendValidation;

import java.util.function.Consumer;

@Component
public class CustomValidator extends LocalValidatorFactoryBean {
    public CustomValidator() {
    }

    @Override
    public void validate(Object target, Errors errors) {
        validate(target, (newTarget)->{
            super.validate(newTarget, errors);
        });
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        validate(target, (newTarget)->{
            super.validate(newTarget, errors, validationHints);
        });
    }

    private void validate(final Object target, Consumer<Object> consumer) {
        consumer.accept(target);
        ExtendValidation extendValidation = target.getClass().getAnnotation(ExtendValidation.class);
        if (extendValidation != null) {
            Class<?>[] classes = extendValidation.value();
            for (Class<?> aClass : classes) {
                Object newTarget = BeanUtil.toBean(target, aClass);
                consumer.accept(newTarget);
            }
        }
    }
}