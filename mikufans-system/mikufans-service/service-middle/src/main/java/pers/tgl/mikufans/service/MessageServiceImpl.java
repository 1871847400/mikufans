package pers.tgl.mikufans.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.domain.comment.UserComment;
import pers.tgl.mikufans.domain.comment.UserCommentArea;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoDanmu;
import pers.tgl.mikufans.model.MessageModel;

import javax.annotation.Resource;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    @Lazy
    @Resource
    private VideoService videoService;
    @Lazy
    @Resource
    private VideoDanmuService videoDanmuService;
    @Lazy
    @Resource
    private UserDynamicService userDynamicService;
    @Lazy
    @Resource
    private UserCommentService userCommentService;
    @Lazy
    @Resource
    private UserCommentAreaService userCommentAreaService;
    @Lazy
    @Resource
    private UserPublishService userPublishService;
    @Lazy
    @Resource
    private UserRateService userRateService;
    @Lazy
    @Resource
    private UserStarService userStarService;

    @Override
    public MessageModel getModel(BusinessType type, Long id) {
        try {
            switch (type) {
                case VIDEO:
                    Video video = videoService.findById(id);
                    return new MessageModel(BusinessType.VIDEO, video.getUri(), video.getTitle());
                case DANMU:
                    VideoDanmu danmu = videoDanmuService.findById(id);
                    return new MessageModel(BusinessType.DANMU, danmu.getUri(), danmu.getContent());
                case COMMENT:
                    UserComment comment = userCommentService.findById(id);
                    UserCommentArea area = userCommentAreaService.findById(comment.getAreaId());
                    MessageModel model = getModel(area.getBusiType(), area.getBusiId());
                    return new MessageModel(BusinessType.COMMENT, model.getUri() + "#" + comment.getId(), comment.getContent());
                case DYNAMIC:
                    UserDynamic dynamic = userDynamicService.findById(id);
                    if (dynamic.getShareId() != 0) {
                        return new MessageModel(BusinessType.DYNAMIC, dynamic.getUri(), dynamic.getShareReason());
                    }
                    return getModel(dynamic.getDynamicType(), dynamic.getTargetId());
                case PUBLISH:
                    UserPublish publish = userPublishService.findById(id);
                    UserDynamic publishDynamic = userDynamicService.findOneBy(UserDynamic::getTargetId, id);
                    return new MessageModel(BusinessType.PUBLISH, publishDynamic.getUri(), publish.getContent());
                case RATE:
                    UserRate userRate = userRateService.findById(id);
                    return new MessageModel(BusinessType.RATE, userRate.getUri(), userRate.getContent());
                case STAR:
                    UserStar userStar = userStarService.findById(id);
                    return new MessageModel(BusinessType.STAR, userStar.getUri(), userStar.getStarName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}