package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileUploadStatus {
    /**
     * 上传中
     */
    UPLOADING(1),
    /**
     * 暂停
     */
    PAUSED(2),
    /**
     * 上传出现异常或到期
     */
    FAILURE(3),
    /**
     * 上传成功
     */
    SUCCESS(4),
    /**
     * 用户取消上传
     */
    CANCEL(5);

    @EnumValue
    private final int code;

    public boolean isDone() {
        return this == FAILURE || this == SUCCESS || this == CANCEL;
    }
}