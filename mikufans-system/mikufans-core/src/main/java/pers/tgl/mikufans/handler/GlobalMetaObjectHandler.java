package pers.tgl.mikufans.handler;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import pers.tgl.mikufans.anno.Encrypt;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.base.BaseEntity;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.function.Supplier;

/**
 * 字段自动填充处理器
 *  注意自动填充依赖于实体,如果使用lambdaUpdate没有传实体,会导致自动填充失效
 *  strictInsertFill 会先检查对象是否有该属性然后判断对应的值为NULL才会填充
 *  setFieldValByName 无论是否有值都会写入
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class GlobalMetaObjectHandler implements MetaObjectHandler {
    @Lazy
    @Resource
    private PasswordEncoder passwordEncoder;

    Supplier<Long> getUserId = () -> {
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (userToken == null) {
            throw new CustomException(Code.NOT_LOGIN);
        }
        //如果当前账号是管理员账号 用户id设置为0,代表是管理员添加的
        if (userToken.getUserType() == 1) {
            return 0L;
        } else {
            return userToken.getId();
        }
    };

    Supplier<Long> getCreateBy = () -> {
        UserToken userToken = SecurityUtils.getContextUserToken();
        //可能由定时任务创建的数据,没有用户上下文
        if (userToken == null) {
            return 0L;
        }
        //如果当前账号不是管理员账号  管理员id为0
        if (userToken.getUserType() != 1) {
            return 0L;
        } else {
            return userToken.getId();
        }
    };

    Supplier<Long> getUpdateBy = () -> {
        UserToken userToken = SecurityUtils.getContextUserToken();
        if (userToken == null) {
            return 0L; //表示系统自动更改
        }
        if (userToken.getUserType() == 1) {
            return userToken.getId();
        }
        //非管理员将不会改变值
        return null;
    };

    @Override
    public void insertFill(MetaObject metaObject) {
        final Date now = new Date();
        this.strictInsertFill(metaObject, BaseEntity.Fields.createTime, Date.class, now);
        this.strictInsertFill(metaObject, BaseEntity.Fields.updateTime, Date.class, now);
        this.strictInsertFill(metaObject, BaseEntity.Fields.remark, String.class, "");
        this.strictInsertFill(metaObject, BaseEntity.Fields.deleteFlag, Integer.class, 0);

        this.strictInsertFill(metaObject, SystemBaseEntity.Fields.createBy, getCreateBy, Long.class);
        this.strictInsertFill(metaObject, SystemBaseEntity.Fields.updateBy, ()->0L, Long.class);

        this.strictInsertFill(metaObject, UserBaseEntity.Fields.userId, getUserId, Long.class);
        defaultFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        严格填充模式下metaObject的填充属性没有默认值才执行填充
//        this.strictUpdateFill(metaObject, BaseEntity.Fields.updateTime, Date.class, new Date());
        //setFieldValByName即使已经有值也会覆盖
        this.setFieldValByName(BaseEntity.Fields.updateTime, new Date(), metaObject);
        this.setFieldValByName(SystemBaseEntity.Fields.updateBy, getUpdateBy.get(), metaObject);
        defaultFill(metaObject);
    }

    private void defaultFill(MetaObject metaObject) {
        Field[] fields = ReflectUtil.getFields(metaObject.getOriginalObject().getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(Encrypt.class) && String.class.isAssignableFrom(field.getType())) {
                Object value = getFieldValByName(field.getName(), metaObject);
                if (value != null && StrUtil.isNotBlank(value.toString())) {
                    this.setFieldValByName(field.getName(), passwordEncoder.encode(value.toString()), metaObject);
                }
            }
        }
    }
}