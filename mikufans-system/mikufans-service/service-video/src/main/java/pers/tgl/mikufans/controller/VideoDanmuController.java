package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.aop.throttle.Throttle;
import pers.tgl.mikufans.domain.video.VideoDanmu;
import pers.tgl.mikufans.domain.video.VideoDanmuColor;
import pers.tgl.mikufans.dto.VideoDanmuDto;
import pers.tgl.mikufans.service.VideoDanmuService;
import pers.tgl.mikufans.vo.VideoDanmuVo;

import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/video/danmu")
@RequiredArgsConstructor
public class VideoDanmuController extends BaseController {
    private final VideoDanmuService videoDanmuService;

    @GetMapping("/list/{partId}")
    @AppLog("获取视频的弹幕列表")
    public List<VideoDanmuVo> list(@PathVariable Long partId,
                                   @RequestParam(required = false) Long minId) {
        return videoDanmuService.listByPartId(partId, minId);
    }

    @PostMapping
    @AppLog("发送弹幕")
    @Throttle(limit = 15, duration = 3, timeUnit = ChronoUnit.MINUTES)
    @RepeatSubmit(interval = 500)
    public VideoDanmu send(@RequestBody @Validated VideoDanmuDto dto) {
        return videoDanmuService.createVideoDanmu(dto);
    }

    @AppLog("删除弹幕")
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        videoDanmuService.removeById(id);
    }

    @GetMapping("/color")
    public List<VideoDanmuColor> colorList() {
        return Db.lambdaQuery(VideoDanmuColor.class)
                .eq(VideoDanmuColor::getDisabled, 0)
                .list();
    }
}