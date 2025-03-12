package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.consts.VideoStatus;
import pers.tgl.mikufans.domain.enums.AuditStatus;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.dto.VideoPartDto;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.VideoPartVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
* @author TGL
* @description 针对表【video_part(视频分集表)】的数据库操作Service
* @createDate 2022-12-31 10:17:31
*/
public interface VideoPartService extends BaseService<VideoPart> {
    /**
     * 获取视频下所有分集
     */
    List<VideoPartVo> getVideoParts(Long videoId);
    /**
     * 获取视频下第一个可以播放的视频
     */
    VideoPartVo getFirst(Long videoId);
    /**
     * 统计能播放或不能播放的数量
     */
    int countCanplay(Long videoId, boolean canplay);
    /**
     * 统计各种审核状态的数量
     */
    Map<AuditStatus, Integer> countAuditStatus(Long videoId);
    /**
     * 统计能播放的总时长
     */
    int sumDuration(Long videoId);
    /**
     * 获取指定分集
     */
    VideoPartVo getVoById(Long id);
    /**
     * 获取最低观看等级
     */
    int getWatchLevel(Long partId);
    /**
     * 查找part使用的resource
     */
    VideoResource findVideoResource(Long partId);
    /**
     * 创建一集视频
     */
    Long createVideoPart(VideoPartDto dto);
    /**
     * 更新视频
     */
    void updateVideoPart(VideoPartDto dto);
    /**
     * 每一个IP或账号每天可以给视频增加一个播放量
     * 如果是游客则只判断IP
     * 如果是用户则判断IP和id
     */
    void recordPlayCount(Long partId, HttpServletRequest request);
    /**
     * 获取自己上传的视频
     */
    Map<VideoStatus, PageImpl<VideoPartVo>> getUploadParts(Long videoId, @Nullable VideoStatus videoStatus);
}