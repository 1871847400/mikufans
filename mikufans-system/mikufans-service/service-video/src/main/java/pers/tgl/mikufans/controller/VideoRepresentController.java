package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.domain.video.VideoRepresent;
import pers.tgl.mikufans.dto.VideoRepresentDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.service.VideoRepresentService;
import pers.tgl.mikufans.vo.VideoRepresentVo;

import java.util.List;

@RestController
@RequestMapping("/video/represent")
@RequiredArgsConstructor
public class VideoRepresentController extends BaseController {
    private final VideoRepresentService videoRepresentService;
    /**
     * 获取代表视频列表
     */
    @GetMapping("/{userId}")
    public List<VideoRepresentVo> list(@PathVariable Long userId) {
        return videoRepresentService.listByUserId(userId);
    }
    /**
     * 创建或修改
     */
    @PostMapping
    @RepeatSubmit(interval = 500)
    public VideoRepresent save(@RequestBody @Validated VideoRepresentDto dto) {
        return videoRepresentService.saveDto(dto);
    }
    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return videoRepresentService.removeById(id);
    }
    /**
     * 修改排序
     */
    @PutMapping("/order")
    public void order(@RequestBody @Validated OrderDto dto) {
        videoRepresentService.changeOrder(dto);
    }
}