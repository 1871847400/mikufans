package pers.tgl.mikufans.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * mp 插入或更新数据时,如果字段不为空，则自动加密
 * 会使用spring容器中提供的PasswordEncoder来进行加密
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {

}