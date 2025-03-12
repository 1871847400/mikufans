package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.dto.VideoDto;
import pers.tgl.mikufans.service.VideoService;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;
import pers.tgl.mikufans.vo.VideoVo;

@RestController
@RequestMapping("/video")
@Validated
@RequiredArgsConstructor
public class VideoController extends BaseController {
    private final VideoService videoService;
    /**
     * 根据id或sid获取指定视频的完整信息
     */
    @GetMapping("/{videoId}")
    public VideoVo getById(@PathVariable String videoId) {
        return videoService.getVoById(videoId);
    }
    /**
     * 创建视频
     */
    @AppLog("创建视频")
    @PostMapping
    @RepeatSubmit(interval = 10_000)
    public Long create(@RequestBody @Validated(Create.class) VideoDto dto) {
        return videoService.createVideo(dto);
    }
    /**
     * 更新视频
     */
    @AppLog("更新视频")
    @RepeatSubmit(interval = 10_000)
    @PutMapping
    public void update(@RequestBody @Validated(Update.class) VideoDto dto) {
        videoService.updateVideo(dto);
    }
    /**
     * 用户删除视频
     */
    @AppLog("删除视频")
    @DeleteMapping("/{videoId}")
    public void remove(@PathVariable Long videoId) {
        videoService.removeById(videoId);
    }
    /**
     * 申请解封视频
     */
    @AppLog("申请解封视频")
    @PutMapping("/apply/{vid}")
    public void apply(@PathVariable Long vid) {
        videoService.apply(vid);
    }
}