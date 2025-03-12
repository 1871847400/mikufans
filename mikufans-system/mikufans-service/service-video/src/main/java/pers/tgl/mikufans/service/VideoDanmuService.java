package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import pers.tgl.mikufans.domain.video.VideoDanmu;
import pers.tgl.mikufans.dto.DanmuImportDto;
import pers.tgl.mikufans.dto.VideoDanmuDto;
import pers.tgl.mikufans.vo.VideoDanmuVo;

import java.util.List;

/**
* @author TGL
* @description 针对表【video_danmu(视频弹幕表)】的数据库操作Service
* @createDate 2023-01-15 10:51:58
*/
public interface VideoDanmuService extends BaseService<VideoDanmu> {
    /**
     * 查找所有最新的弹幕(会有最大量限制,只取最新的一部分)
     */
    List<VideoDanmuVo> listByPartId(Long partId, @Nullable Long minId);
    /**
     * 创建一条弹幕
     */
    VideoDanmu createVideoDanmu(@Validated VideoDanmuDto dto);
    /**
     * 批量导入弹幕
     */
    String importBatch(DanmuImportDto dto);
}