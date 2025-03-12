package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.video.Video;

/**
* @author TGL
* @description 针对表【video(视频表)】的数据库操作Mapper
* @createDate 2022-12-31 10:17:31
* @Entity .domain.Video
*/
@Mapper
public interface VideoMapper extends MPJBaseMapper<Video> {

}