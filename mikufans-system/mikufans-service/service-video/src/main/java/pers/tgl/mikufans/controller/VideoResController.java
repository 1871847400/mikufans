package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.service.VideoResourceService;

@RestController
@RequestMapping("/video/resource")
@Validated
@Slf4j
@RequiredArgsConstructor
public class VideoResController extends BaseController {
    private final VideoResourceService videoResourceService;
    /**
     * 根据id查找
     */
    @GetMapping("/{id}")
    public VideoResource getById(@PathVariable Long id) {
        return videoResourceService.getById(id);
    }
    /**
     * 通过HASH值查找
     */
    @GetMapping("/hash/{hash}")
    public VideoResource findByHash(@PathVariable String hash) {
        return videoResourceService.getOneBy(VideoResource::getMd5, hash);
    }
}