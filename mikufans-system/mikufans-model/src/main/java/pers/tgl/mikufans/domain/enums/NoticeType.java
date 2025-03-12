package pers.tgl.mikufans.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pers.tgl.mikufans.form.OptionsProvider;
import pers.tgl.mikufans.model.Option;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum NoticeType implements OptionsProvider {
    /**
     * 公告
     */
    BROADCAST(1),
    /**
     * 视频举报成功
     */
    VIDEO_REPORT_SUCCESS(101),
    /**
     * 视频举报失败
     */
    VIDEO_REPORT_FAIL(102),
    /**
     * 视频审核成功
     */
    VIDEO_AUDIT_SUCCESS(103),
    /**
     * 视频审核失败
     */
    VIDEO_AUDIT_FAIL(104),
    /**
     * 视频违规下架
     */
    VIDEO_VIOLATION(105),
    /**
     * 视频解封申请
     */
    VIDEO_APPLY(106),
    /**
     * 弹幕举报成功
     */
    DANMU_REPORT_SUCCESS(201),
    /**
     * 弹幕举报失败
     */
    DANMU_REPORT_FAIL(202),
    /**
     * 弹幕违规
     */
    DANMU_VIOLATION(203),
    /**
     * 评论举报成功
     */
    COMMENT_REPORT_SUCESS(301),
    /**
     * 评论举报失败
     */
    COMMENT_REPORT_FAIL(302),
    /**
     * 评论违规
     */
    COMMENT_VIOLATION(303);

    @EnumValue
    private final int value;

    @Override
    public List<Option> getOptions() {
        return Collections.singletonList(new Option("公告", BROADCAST.name()));
    }
}