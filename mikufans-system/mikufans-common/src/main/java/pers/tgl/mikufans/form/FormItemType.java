package pers.tgl.mikufans.form;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FormItemType {
    /**
     * input 输入字符串
     */
    TEXT(true),
    /**
     * 文本域
     */
    TEXTAREA(true),
    /**
     * 密码输入框
     */
    PASSWORD(true),
    /**
     * 数字输入框
     */
    NUMBER(false),
    /**
     * 下拉框 单项选择
     */
    SELECT(false),
    /**
     * 单选框
     */
    RADIO(false),
    /**
     * 上传图片
     */
    IMAGE(false),
    /**
     * 日期时间
     */
    DATE(false),
    /**
     * 颜色选择
     */
    COLOR(true),
    /**
     * 选择图标
     */
    ICON(true);

    /**
     * 是否为文本输入类型
     */
    private final boolean text;
}