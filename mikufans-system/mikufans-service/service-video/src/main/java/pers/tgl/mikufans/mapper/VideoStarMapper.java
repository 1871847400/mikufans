package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.video.VideoStar;

/**
* @author TGL
* @description 针对表【video_star(视频收藏记录表)】的数据库操作Mapper
* @createDate 2023-01-20 16:09:02
* @Entity pers.tgl.mikufans.domain.video.VideoStar
*/
@Mapper
public interface VideoStarMapper extends MPJBaseMapper<VideoStar> {
}