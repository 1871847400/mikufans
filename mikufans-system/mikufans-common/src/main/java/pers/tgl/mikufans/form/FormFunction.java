package pers.tgl.mikufans.form;

import cn.hutool.core.date.DateUtil;

/**
 * 用于填充默认值时使用函数
 */
public class FormFunction {
    public String now() {
        return DateUtil.now();
    }

    public String Null() {
        return null;
    }
}