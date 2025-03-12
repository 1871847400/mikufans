package pers.tgl.mikufans.form;

/**
 * 设置部分表单属性在什么时候生效
 */
public enum Scope {
    NONE, //任何时候都不生效
    CREATE, //创建时生效
    UPDATE, //更新时生效
    BOTH //创建和更新时都生效
}