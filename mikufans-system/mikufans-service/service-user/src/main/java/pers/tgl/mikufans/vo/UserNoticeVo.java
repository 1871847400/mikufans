package pers.tgl.mikufans.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.enums.NoticeType;
import pers.tgl.mikufans.domain.system.SysNotice;
import pers.tgl.mikufans.domain.user.UserNotice;
import pers.tgl.mikufans.util.MyUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserNoticeVo extends UserNotice {
    /**
     * 对应的系统通知
     */
    @JsonIgnore
    private SysNotice sysNotice;
    /**
     * 最终显示的通知标题
     */
    private String title;
    /**
     * 最终显示的通知内容
     */
    private String content;
    /**
     * 最终访问链接
     */
    private String uri;
    /**
     * 通知发布时间
     */
    public String getPublishTime() {
        if (getSysNotice() != null && getSysNotice().getNoticeType() == NoticeType.BROADCAST) {
            return MyUtils.formatHumanTime(getSysNotice().getEnableTime());
        }
        return MyUtils.formatHumanTime(getCreateTime());
    }
}