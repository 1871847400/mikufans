package pers.tgl.mikufans.consts;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferMode {
    /**
     * 不转存
     */
    NONE(0),
    /**
     * 转存到网盘
     */
    CLOUD(1),
    /**
     * 使用对象存储
     */
    OSS(2);

    @EnumValue
    private final int code;
}