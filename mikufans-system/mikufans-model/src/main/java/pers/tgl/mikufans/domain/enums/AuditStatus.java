package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AuditStatus {
    /**
     * 未审核
     */
    UNKNOWN(0, "未审核"),
    /**
     * 通过
     */
    SUCCESS(1, "通过"),
    /**
     * 未通过
     */
    FAIL(2, "未通过");

    @EnumValue
    private final int code;
    private final String message;
}