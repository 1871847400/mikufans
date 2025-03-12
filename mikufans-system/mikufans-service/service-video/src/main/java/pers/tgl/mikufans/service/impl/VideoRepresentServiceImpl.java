package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoRepresent;
import pers.tgl.mikufans.dto.VideoRepresentDto;
import pers.tgl.mikufans.event.VideoEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.VideoRepresentMapper;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.service.VideoRepresentService;
import pers.tgl.mikufans.service.VideoService;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.VideoRepresentVo;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VideoRepresentServiceImpl extends BaseServiceImpl<VideoRepresent, VideoRepresentMapper> implements VideoRepresentService {
    private final VideoService videoService;

    @Override
    public List<VideoRepresentVo> listByUserId(Long userId) {
        return wrapper()
                .selectAssociation(Video.class, VideoRepresentVo::getVideo)
                .innerJoin(Video.class, Video::getId, VideoRepresent::getVideoId)
                .eq(VideoRepresent::getUserId, userId)
                .orderByAsc(VideoRepresent::getSort)
                .orderByDesc(VideoRepresent::getCreateTime)
                .list(VideoRepresentVo.class);
    }

    @Override
    public VideoRepresent saveDto(VideoRepresentDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        VideoRepresent model = lambdaQuery()
                .eq(VideoRepresent::getVideoId, dto.getVideoId())
                .eq(VideoRepresent::getUserId, contextUserId)
                .one();
        if (model == null) {
            long count = countBy(VideoRepresent::getUserId, contextUserId);
            if (count >= 3) {
                throw new CustomException("超过个数限制");
            }
            Video video = videoService.getById(dto.getVideoId());
            if (video == null) {
                throw new CustomException("视频不存在");
            }
            if (!Objects.equals(video.getUserId(), contextUserId)) {
                throw new CustomException(Code.FORBIDDEN);
            }
            model = BeanUtil.toBean(dto, VideoRepresent.class);
            model.setSort((int) (count + 1));
        } else {
            model.setReason(dto.getReason());
        }
        saveOrUpdate(model);
        return model;
    }

    @Override
    public void changeOrder(OrderDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        int i = 1;
        for (Long id : dto.getIds()) {
            boolean update = lambdaUpdate().eq(VideoRepresent::getId, id)
                    .eq(VideoRepresent::getUserId, contextUserId)
                    .set(VideoRepresent::getSort, i)
                    .update(new VideoRepresent());
            if (update) {
                i++;
            }
        }
    }

    @EventListener(VideoEvent.class)
    public void listen(VideoEvent e) {
        if (e.isDelete()) {
            VideoRepresent entity = getOneBy(VideoRepresent::getVideoId, e.getEntity().getId());
            removeById(entity);
        }
    }
}