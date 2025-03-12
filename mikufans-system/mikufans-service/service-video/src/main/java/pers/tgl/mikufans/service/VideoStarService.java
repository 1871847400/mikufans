package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoStar;
import pers.tgl.mikufans.dto.VideoStarDto;
import pers.tgl.mikufans.params.VideoStarParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.VideoStarVo;

/**
* @author TGL
* @description 针对表【video_star(视频收藏记录表)】的数据库操作Service
* @createDate 2023-01-20 16:09:02
*/
public interface VideoStarService extends BaseService<VideoStar> {
    /**
     * 保存视频收藏数据
     * @return 操作完后,用户是否有收藏过该视频
     */
    boolean saveDto(VideoStarDto dto);
    /**
     * 搜索收藏夹内的视频
     */
    PageImpl<VideoStarVo> search(VideoStarParams search);
}