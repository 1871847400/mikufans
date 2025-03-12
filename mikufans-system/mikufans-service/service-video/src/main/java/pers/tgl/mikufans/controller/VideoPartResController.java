package pers.tgl.mikufans.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.model.UserToken;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.SecurityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

/**
 * 视频资源文件控制器
 * 大部分请求由hls.js发起
 */
@RestController
@RequestMapping("/video/resource")
@RequiredArgsConstructor
@Slf4j
public class VideoPartResController extends BaseController {
    private final VideoService videoService;
    private final VideoPartService videoPartService;
    private final VideoResourceService videoResourceService;
    private final UserService userService;
    private final UserDynamicService userDynamicService;

    /**
     * 下载画质索引文件
     */
    @GetMapping("/download/{partId}/master")
    public void downloadMaster(@PathVariable Long partId, HttpServletResponse response) throws IOException {
        try {
            VideoPart part = videoPartService.getById(partId);
            if (part == null) {
                throw new CustomException("视频不存在");
            }
            if (part.getCanplay() == 0) {
                throw new CustomException("当前视频暂时无法观看");
            }
            UserToken userToken = SecurityUtils.getContextUserToken();
            UserDynamic dynamic = userDynamicService.getOneBy(UserDynamic::getTargetId, part.getVideoId());
            if (dynamic != null && dynamic.getVisible() == 0) {
                if (userToken == null || !Objects.equals(userToken.getId(), part.getUserId())) {
                    throw new CustomException("该视频仅限上传者观看");
                }
            }
            checkUserLevel(partId);
            //记录播放次数
            videoPartService.recordPlayCount(partId, getRequest());
            respond(partId,"master.m3u8");
        } catch (CustomException e) {
            //不能使用403,会没有响应体
            response.sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("获取视频索引文件异常", e);
            response.sendError(HttpStatus.HTTP_INTERNAL_ERROR, "服务器异常");
        }
    }
    /**
     * 下载分片
     */
    @GetMapping("/download/{partId}/{level}/{uri}")
    public void downloadSegment(@PathVariable Long partId,
                                @PathVariable String level,
                                @PathVariable String uri) throws IOException {
        try {
            VideoResource resource = videoPartService.findVideoResource(partId);
            if (resource == null || resource.getPending() == 1) {
                throw new CustomException("视频文件不存在");
            }
            videoResourceService.fetchSegment(resource, VideoQuality.valueOf(level.toUpperCase()), uri);
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }
    /**
     * 下载指定画质的播放列表
     */
    @GetMapping("/download/{partId}/{level}/playlist.m3u8")
    public void downloadPlaylist(@PathVariable Long partId, @PathVariable String level) throws IOException {
        try {
            respond(partId, VideoQuality.valueOf(level.toUpperCase()).key() + File.separator + "playlist.m3u8");
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }
    /**
     * 下载指定画质的密码
     */
    @GetMapping("/download/{partId}/{level}/key")
    public void downloadKeyfile(@PathVariable Long partId, @PathVariable String level) throws IOException {
        try {
            respond(partId, "key");
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }
    /**
     * 下载视频缩略图VTT配置
     */
    @GetMapping("/download/{partId}/thumbnails.vtt")
    public void downloadThumbnails(@PathVariable Long partId) throws IOException {
        try {
            respond(partId, "thumbnails.vtt");
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }
    /**
     * 下载视频缩略图
     */
    @GetMapping("/download/{partId}/thumbnails.jpg")
    public void downloadThumbnailsJpg(@PathVariable Long partId) throws IOException {
        try {
            respond(partId, "thumbnails.jpg");
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }
    /**
     * 下载视频字幕文件
     */
    @GetMapping("/download/{partId}/subtitles/{filename}")
    public void downloadSubtitle(@PathVariable Long partId, @PathVariable String filename) throws IOException {
        try {
            respond(partId, "subtitles" + File.separator + filename);
        } catch (CustomException e) {
            getResponse().sendError(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
        }
    }

    private void respond(Long partId, String path) throws IOException {
        HttpServletResponse response = getResponse();
        Long resId = videoPartService.getColumnValue(partId, VideoPart::getResId);
        File file = videoResourceService.getStorage(resId, path);
        if (FileUtil.isEmpty(file)) {
            throw new CustomException("视频相关文件不存在");
        }
        MyUtils.writeFile(response, file, Duration.ofSeconds(30));
    }

    private void checkUserLevel(Long partId) {
        int minLevel = videoPartService.getWatchLevel(partId);
        if (minLevel > 0) {
            UserToken userToken = SecurityUtils.getContextUserToken();
            if (userToken == null) {
                throw new CustomException("该视频需要登录后才能观看");
            }
            if (userService.getUserLevel(userToken.getId()) < minLevel) {
                throw new CustomException("当前视频需要您达到" + minLevel +"级才能播放");
            }
        }
    }
}