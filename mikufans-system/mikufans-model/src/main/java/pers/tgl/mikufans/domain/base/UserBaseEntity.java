package pers.tgl.mikufans.domain.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.validator.group.Update;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
public abstract class UserBaseEntity extends SystemBaseEntity {
    /**
     * 雪花id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @NotNull(groups = Update.class, message = "ID不能为空")
    private Long id;
    /**
     * 用户id (数据创建人)
     */
    @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
    private Long userId;
    /**
     * 乐观锁
     */
    @Version
    @JsonIgnore
    private Integer version;
    /**
     * 序列化基础用户信息,详情需要调用controller
     */
    public User getUser() {
        return getUserBase(userId);
    }

    public static User getUserBase(Object userId) {
        return new MPJLambdaWrapper<>(User.class)
                .disableLogicDel()
                .select(User::getId,
                        User::getNickname,
                        User::getAvatarId,
                        User::getLevel)
                .eq(User::getId, userId)
                .one();
    }
}