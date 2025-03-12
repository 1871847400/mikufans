package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.video.VideoPart;

/**
* @author TGL
* @description 针对表【video_part(视频分集表)】的数据库操作Mapper
* @createDate 2022-12-31 10:17:31
* @Entity .domain.VideoPart
*/
@Mapper
public interface VideoPartMapper extends MPJBaseMapper<VideoPart> {

}