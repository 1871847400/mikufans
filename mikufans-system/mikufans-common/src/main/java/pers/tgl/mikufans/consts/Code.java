package pers.tgl.mikufans.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务错误代码,需要和HTTP错误码区分开,不能使用200,400等
 */
@Getter
@AllArgsConstructor
public enum Code {
    /**
     * 一切正常
     */
    OK(0, null),
    /**
     * 一般错误
     */
    ERROR(1, "错误"),
    /**
     * 需要登录后才能请求,代表没有token或token失效
     */
    NOT_LOGIN(2, "请先登录"),
    /**
     * 手动重定向,返回url前端需要手动重定向
     */
    REDIRECT(3, "重定向"),
    /**
     * 访问别人隐私数据
     */
    PRIVACY_DENY(4, "无权访问隐私"),
    /**
     * token到期,请刷新
     */
    TOKEN_EXPIRED(5, "身份认证已到期,请刷新后重试！"),
    /**
     * 本次请求需要验证码
     */
    CAPTCHA(6, "请通过验证码"),
    /**
     * 权限不足
     */
    FORBIDDEN(7, "您无权访问"),
    /**
     * 硬币不足
     */
    LESS_COIN(8, "硬币不足"),
    /**
     * 实体不存在
     */
    ENTITY_NOT_FOUND(9, "访问用户不存在"),
    /**
     * 下载的资源不存在
     */
    RESOURCE_NOT_FOUND(10, "访问资源不存在"),
    /**
     * 访问的等级不够
     */
    LOW_LEVEL(11, "用户等级不足"),
    /**
     * 服务器内部错误
     */
    SERVER_ERROR(12, "服务器内部异常"),
    /**
     * 重复表单提交
     */
    REPEAT_SUBMIT(13, "请勿重复提交"),
    /**
     * 数据已存在
     */
    DATA_EXISTED(14, "请勿重复添加"),
    /**
     * 禁止登录
     */
    AUTH_DISABLED(15, "您已被禁止登录");
    /**
     * 状态码
     */
    private final int value;
    /**
     * 默认错误消息
     */
    private final String msg;
}