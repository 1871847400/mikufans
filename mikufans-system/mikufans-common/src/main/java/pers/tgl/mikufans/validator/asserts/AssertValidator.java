package pers.tgl.mikufans.validator.asserts;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AssertValidator implements ConstraintValidator<Assert, Object> {
    private Assert instance;

    @Override
    public void initialize(Assert constraintAnnotation) {
        instance = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String str : instance.value()) {
            if (str.equals(value)) {
                return true;
            }
        }
        return true;
    }
}