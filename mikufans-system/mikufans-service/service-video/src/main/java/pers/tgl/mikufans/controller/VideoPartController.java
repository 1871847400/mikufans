package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.consts.VideoStatus;
import pers.tgl.mikufans.dto.VideoPartDto;
import pers.tgl.mikufans.service.VideoPartService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;
import pers.tgl.mikufans.vo.VideoPartVo;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/video/part")
@RequiredArgsConstructor
public class VideoPartController extends BaseController {
    private final VideoPartService videoPartService;

    @GetMapping("/{id}")
    public VideoPartVo getById(@PathVariable Long id) {
        return videoPartService.getVoById(id);
    }

    @AppLog("搜索视频分集")
    @GetMapping("/search")
    public List<VideoPartVo> search(@RequestParam Long videoId) {
        return videoPartService.getVideoParts(videoId);
    }

    @GetMapping("/first/{videoId}")
    public VideoPartVo getFirst(@PathVariable Long videoId) {
        return videoPartService.getFirst(videoId);
    }

    @AppLog("创建视频分集")
    @PostMapping
    @RepeatSubmit(interval = 5_000)
    public void create(@RequestBody @Validated(Create.class) VideoPartDto dto) {
        videoPartService.createVideoPart(dto);
    }

    @AppLog("更新视频分集")
    @PutMapping
    public void update(@RequestBody @Validated(Update.class) VideoPartDto dto) {
        videoPartService.updateVideoPart(dto);
    }

    @AppLog("删除视频分集")
    @DeleteMapping("/{partId}")
    public void remove(@PathVariable Long partId) {
        videoPartService.removeById(partId);
    }

    @GetMapping("/upload")
    public Map<VideoStatus, PageImpl<VideoPartVo>> getUpload(@RequestParam Long videoId,
                                                             @RequestParam(required = false) VideoStatus status) {
        return videoPartService.getUploadParts(videoId, status);
    }
}