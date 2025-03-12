package pers.tgl.mikufans.validator.role;

import cn.hutool.core.util.ArrayUtil;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class RoleValidator implements ConstraintValidator<Role, Object> {
    private Role instance;
    @Override
    public void initialize(Role constraintAnnotation) {
        instance = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (ArrayUtil.isNotEmpty(instance.ignores())) {
            for (String ignore : instance.ignores()) {
                if (Objects.toString(value).equals(ignore)) {
                    return true;
                }
            }
        }
        return SecurityUtils.hasAnyRole(instance.value());
    }
}