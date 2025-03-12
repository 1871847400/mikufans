package pers.tgl.mikufans.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import pers.tgl.mikufans.consts.Code;

import java.io.Serializable;

/**
 * 统一响应体
 */
@Data
@AllArgsConstructor
public final class R<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    public static <T> R<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(Code.OK.getValue(), msg, data);
    }

    public static R<?> error(String msg) {
        return new R<>(Code.ERROR.getValue(), msg, null);
    }

    public static R<?> error(Code code) {
        return new R<>(code.getValue(), code.getMsg(), null);
    }
}