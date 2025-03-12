package pers.tgl.mikufans.validator.datetime;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

public class DateTimeLimitValidator implements ConstraintValidator<DateTimeLimit, Object> {
    private DateTimeLimit instance;

    @Override
    public void initialize(DateTimeLimit constraintAnnotation) {
        instance = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof Date)) {
            return true;
        }
        Date date = (Date) value;
        DateTime offset = DateUtil.offset(new Date(), instance.unit(), instance.offset());
        if (instance.type() == DateTimeLimit.Type.PAST) {
            return !offset.isBefore(date);
        } else if (instance.type() == DateTimeLimit.Type.FUTURE) {
            return !offset.isAfter(date);
        }
        return true;
    }
}