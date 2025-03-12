package pers.tgl.mikufans.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.model.SearchParams;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserCommentParams extends SearchParams {
    @NotNull
    private Long areaId;
    /**
     * 0: 按照热度分数排序
     * 1: 按照时间排序
     */
    @Range(min = 0 , max = 1)
    private int order = 0;
    /**
     * 限制在这个时间之后发送的评论
     */
    private Date afterTime;
    /**
     * 附带热评数量
     */
    @Max(value = 5, message = "附带热评最多5个")
    private Integer hots = 0;
    /**
     * 限制回复的评论 (0表示父评论)
     */
    private Long replyId;
    /**
     * 限制回复的目标用户id
     */
    private Long replyUserId;
    /**
     * 限制发送评论的用户id
     */
    private Long userId;
}