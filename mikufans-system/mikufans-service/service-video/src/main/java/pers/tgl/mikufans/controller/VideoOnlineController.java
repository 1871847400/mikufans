package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.manager.VideoWatchManager;

@RestController
@RequestMapping("/video/online")
@RequiredArgsConstructor
public class VideoOnlineController extends BaseController {
    private final VideoWatchManager videoWatchManager;
    /**
     * 正在观看视频的人数
     */
    @GetMapping("/{videoId}")
    public int getOnline(@PathVariable Long videoId) {
        return videoWatchManager.getOnline(videoId);
    }
}