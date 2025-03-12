package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CommentFlag {
    /**
     * 正常,所有人都可评论
     */
    NORMAL(0),
    /**
     * 精选评论,需要经过UP主挑选
     */
    CHOICE(1),
    /**
     * 关闭评论区，所有人不可评论
     */
    DISABLED(2);

    @EnumValue
    private final int code;
}