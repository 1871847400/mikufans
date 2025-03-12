package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.dto.VideoWatchDto;
import pers.tgl.mikufans.params.HistorySearch;
import pers.tgl.mikufans.service.VideoWatchHistoryService;
import pers.tgl.mikufans.vo.VideoWatchHistoryVo;

@RestController
@RequestMapping("/video/history")
@RequiredArgsConstructor
public class VideoHistoryController extends BaseController {
    private final VideoWatchHistoryService videoWatchHistoryService;

    @GetMapping("/search")
    public IPage<VideoWatchHistoryVo> search(@Validated HistorySearch params) {
        return videoWatchHistoryService.search(params);
    }

    @PostMapping
    public void record(@RequestBody @Validated VideoWatchDto dto) {
        videoWatchHistoryService.create(dto);
    }

    @DeleteMapping("/clear")
    public void removeAll() {
        videoWatchHistoryService.removeAllByUserId();
    }

    @DeleteMapping("/{id}")
    public Boolean remove(@PathVariable Long id) {
        return videoWatchHistoryService.removeById(id);
    }
}