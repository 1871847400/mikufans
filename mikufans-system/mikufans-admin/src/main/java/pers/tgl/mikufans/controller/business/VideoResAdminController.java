package pers.tgl.mikufans.controller.business;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.service.VideoPartService;
import pers.tgl.mikufans.service.VideoResourceService;
import pers.tgl.mikufans.util.MyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * 审核视频的专用接口
 */
@RestController
@RequestMapping("/admin/video/resource")
@RequiredArgsConstructor
@Slf4j
public class VideoResAdminController extends BaseController {
    private final VideoPartService videoPartService;
    private final VideoResourceService videoResourceService;

    /**
     * 下载画质索引文件
     */
    @PreAuthorize("hasPermission('video_audit', 'select')")
    @GetMapping("/download/{partId}/master")
    public void downloadMaster(@PathVariable Long partId) throws IOException {
        respond(partId,"master.m3u8");
    }
    /**
     * 下载分片
     */
    @PreAuthorize("hasPermission('video_audit', 'select')")
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
    @PreAuthorize("hasPermission('video_audit', 'select')")
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
    @PreAuthorize("hasPermission('video_audit', 'select')")
    @GetMapping("/download/{partId}/{level}/key")
    public void downloadKeyfile(@PathVariable Long partId, @PathVariable String level) throws IOException {
        try {
            respond(partId, "key");
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
}