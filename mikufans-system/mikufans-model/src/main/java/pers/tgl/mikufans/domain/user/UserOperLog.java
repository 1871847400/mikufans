package pers.tgl.mikufans.domain.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

/**
 * 用户操作日志表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldNameConstants
@PermFlag(name = "用户日志", group = PermGroup.LOG)
public class UserOperLog extends UserBaseEntity {
    /**
     * 一般操作类型
     */
    public static final int OPER_TYPE_NORMAL = 0;
    /**
     * 登录操作类型
     */
    public static final int OPER_TYPE_LOGIN = 1;
    /**
     * 操作标题
     */
    private String title;
    /**
     * 用户名
     */
    private String username;
    /**
     * IP地址
     */
    private String ipaddr;
    /**
     * 客户端类型 0:未知 1:PC 2:APP
     */
    private Integer clientType;
    /**
     * 操作代理
     */
    private String userAgent;
    /**
     * 操作状态 0成功 1失败
     */
    private Integer operStatus;
    /**
     * 操作类型
     */
    private Integer operType;
    /**
     * 请求资源路径
     */
    private String reqUri;
    /**
     * 请求参数
     */
    private String reqParams;
    /**
     * 请求方式
     */
    private String reqMethod;
    /**
     * 方法路径
     */
    private String methodPath;
    /**
     * 消耗时间(毫秒)
     */
    private Integer costTime;
    /**
     * 用户类型
     */
    private Integer userType;
}