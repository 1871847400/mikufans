package pers.tgl.mikufans.params;

import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.tgl.mikufans.consts.Consts;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.model.SearchParams;
import pers.tgl.mikufans.search.SearchDateRange;
import pers.tgl.mikufans.search.VideoDurationRange;
import pers.tgl.mikufans.search.VideoSearchSort;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * 视频搜索参数，所有参数均为可选项
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoParams extends SearchParams {
    /**
     * 搜索关键字: 视频标题, SID
     */
    @Length(max = Consts.VIDEO_TITLE_MAX_LENGTH, message = "搜索关键字过长")
    private String keyword;
    /**
     * 限制类型
     */
    private VideoType type;
    /**
     * 排序
     */
    @NotNull
    private VideoSearchSort sort = VideoSearchSort.SCORE;
    /**
     * 要求指定用户id上传的
     */
    private Long userId;
    /**
     * 限制为关注用户发布的视频
     */
    private Boolean searchFollow;
    /**
     * 限制标签
     */
    @Length(max = Consts.VIDEO_TAG_MAX_LENGTH)
    private String tag;
    /**
     * 限制发布时间区间
     * SearchDateRange 或 2024-08-01_2024-08-02
     */
    private String date;
    /**
     * 限制分区,如果为父分区id,则会转为判断所有子分区的id
     */
    private Long channelId;
    /**
     * 限制时长 (分钟)
     * 1: 10分钟以内
     * 2: 10-30
     * 3: 30-80
     * 4: 60以上
     */
    @NotNull
    private VideoDurationRange duration = VideoDurationRange.ALL;

    @AssertTrue(message = "日期格式不正确")
    public boolean isValid() {
        if (StrUtil.isNotBlank(date)) {
            SearchDateRange dateRange = EnumUtil.fromStringQuietly(SearchDateRange.class, date);
            if (dateRange == null) {
                return ReUtil.isMatch("\\d{4}-\\d{2}-\\d{2}_\\d{4}-\\d{2}-\\d{2}", date);
            }
        }
        return true;
    }
}