package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.dto.VideoStarDto;
import pers.tgl.mikufans.params.VideoStarParams;
import pers.tgl.mikufans.service.VideoStarService;
import pers.tgl.mikufans.vo.VideoStarVo;

/**
 * 用户收藏视频到收藏夹
 */
@RestController
@RequestMapping("/video/star")
@Validated
@RequiredArgsConstructor
public class VideoStarController extends BaseController {
    private final VideoStarService videoStarService;

    /**
     * 新增或删除视频收藏
     */
    @PostMapping
    public boolean save(@RequestBody @Validated VideoStarDto dto) {
        return videoStarService.saveDto(dto);
    }
    /**
     * 获取收藏夹收藏的视频
     */
    @GetMapping("/search")
    public IPage<VideoStarVo> search(@Validated VideoStarParams search) {
        return videoStarService.search(search);
    }
}