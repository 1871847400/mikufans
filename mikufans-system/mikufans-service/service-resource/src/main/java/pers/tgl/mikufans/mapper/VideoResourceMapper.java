package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.video.VideoResource;

/**
* @author TGL
* @description 针对表【video_resource(视频资源表)】的数据库操作Mapper
* @createDate 2023-01-16 13:56:59
* @Entity pers.tgl.mikufans.domain/res.VideoResource
*/
@Mapper
public interface VideoResourceMapper extends MPJBaseMapper<VideoResource> {

}