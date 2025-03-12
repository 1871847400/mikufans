package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoRepresent;
import pers.tgl.mikufans.dto.VideoRepresentDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.vo.VideoRepresentVo;

import java.util.List;

/**
* @author TGL
* @createDate 2024-9-1
*/
public interface VideoRepresentService extends BaseService<VideoRepresent> {
    /**
     * 列出所有
     */
    List<VideoRepresentVo> listByUserId(Long userId);
    /**
     * 创建或修改
     */
    VideoRepresent saveDto(VideoRepresentDto dto);
    /**
     * 改变排序
     */
    void changeOrder(OrderDto dto);
}