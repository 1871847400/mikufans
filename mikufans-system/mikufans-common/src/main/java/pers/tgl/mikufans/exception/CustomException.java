package pers.tgl.mikufans.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.util.R;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomException extends RuntimeException {
    private final R<?> result;

    public CustomException() {
        this(Code.ERROR);
    }

    public CustomException(String message) {
        this(Code.ERROR, message);
    }

    public CustomException(R<?> result) {
        super(result.getMsg());
        this.result = result;
    }

    public CustomException(Code code) {
        this(code, code.getMsg());
    }

    public CustomException(Code code, String message) {
        super(message);
        this.result = new R<>(code.getValue(), message, null);
    }
}