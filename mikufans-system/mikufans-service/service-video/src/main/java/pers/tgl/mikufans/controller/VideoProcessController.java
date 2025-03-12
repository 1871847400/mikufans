package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.service.VideoProcessService;

import java.util.Map;

@RequestMapping("/video/process")
@RestController
@RequiredArgsConstructor
public class VideoProcessController extends BaseController {
    private final VideoProcessService videoProcessService;

    @GetMapping("/status")
    @Cacheable(value = "video:process:status#30s")
    public Map<String, String> getStatus() {
        return videoProcessService.getProcessStatus();
    }
}