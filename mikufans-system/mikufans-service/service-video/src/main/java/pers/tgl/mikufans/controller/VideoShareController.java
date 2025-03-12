package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.service.VideoShareService;
import pers.tgl.mikufans.validator.db.DBExists;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/video/share")
public class VideoShareController extends BaseController {
    private final VideoShareService videoShareService;
    /**
     * 创建分享链接
     */
    @AppLog("创建视频分享")
    @PostMapping("/{videoId}")
    public String createShare(@PathVariable @DBExists(Video.class) Long videoId) {
        return videoShareService.createShare(videoId).getShareCode();
    }
    /**
     * 使用分享码
     */
    @AppLog("使用视频分享")
    @PutMapping("/{code}")
    public void useShare(@PathVariable String code) {
        videoShareService.useShare(code);
    }
}