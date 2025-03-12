package pers.tgl.mikufans.model;

import lombok.Data;

/**
 * 验证码对象
 */
@Data
public class VerificationCode {
    /**
     * 验证码id
     */
    private String id;
    /**
     * 正确值
     */
    private Float value;
    /**
     * 生成时间
     */
    private Long timestamp;
    /**
     * 是否已经通过验证
     */
    private Boolean pass;
}