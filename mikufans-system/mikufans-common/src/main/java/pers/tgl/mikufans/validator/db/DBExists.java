package pers.tgl.mikufans.validator.db;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 自动判断对应主键是否存在数据库,主要用于id列
 * 注意不检测NULL,用@NotNull组合判断
 * TYPE_USE 指泛型 List<@DBExists Integer>
 */
@Documented
@Constraint(validatedBy = { DBExistsValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(DBExists.List.class)
public @interface DBExists {
    /**
     * 检测的model类型
     * 必须注入相关类的mapper
     */
    Class<?> value();
    /**
     * 检测列的列名，留空会自动寻找该类的主键
     */
    String keyColumn() default "";
    /**
     * 跳过检查的id列表,例如id=0的时候跳过
     */
    String[] ignores() default {};
    /**
     * 检查是否有操作权限,需要操作的用户id相同
     */
    boolean checkUserId() default false;
    /**
     * 用户id列名,需要checkUserId=true才有用
     */
    String userIdColumn() default "user_id";
    /**
     * 结果取反,如果数据存在则异常
     */
    boolean errorInExist() default false;

    String message() default "无效的数据";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        DBExists[] value();
    }
}