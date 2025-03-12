package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VideoLockFlag {
    /**
     * 未锁定状态,一切正常 可以被搜索和播放
     */
    NO_LOCK(0),
    /**
     * 视频准备中,当至少有一个part上传完成且能够播放时改为未锁定状态
     */
    PREPARE(1),
    /**
     * 审核中
     */
    AUDIT(2),
    /**
     * 被管理员封禁
     */
    BAN(4);

    @EnumValue
    private final int code;
}