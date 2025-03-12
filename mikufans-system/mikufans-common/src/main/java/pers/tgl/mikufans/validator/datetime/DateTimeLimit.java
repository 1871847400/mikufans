package pers.tgl.mikufans.validator.datetime;

import cn.hutool.core.date.DateField;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = { DateTimeLimitValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(DateTimeLimit.List.class)
public @interface DateTimeLimit {
    public enum Type {
        PAST, FUTURE
    }

    Type type();

    int offset() default 0;

    DateField unit() default DateField.SECOND;

    String message() default "时间不在允许的范围内";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateTimeLimit[] value();
    }
}