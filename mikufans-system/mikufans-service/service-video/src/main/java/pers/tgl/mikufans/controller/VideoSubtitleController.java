package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.service.VideoSubtitleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video/subtitle")
public class VideoSubtitleController extends BaseController {
    private final VideoSubtitleService videoSubtitleService;

    @GetMapping("/content/{id}")
    public String getSubtitleText(@PathVariable Long id) {
        return videoSubtitleService.getSubtitleText(id);
    }

    @AppLog("删除字幕")
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        videoSubtitleService.removeById(id);
    }
}