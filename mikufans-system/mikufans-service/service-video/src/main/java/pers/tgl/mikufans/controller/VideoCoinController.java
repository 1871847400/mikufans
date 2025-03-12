package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.service.VideoCoinService;

@RestController
@RequestMapping("/video/coin")
@Validated
@RequiredArgsConstructor
public class VideoCoinController extends BaseController {
    private final VideoCoinService videoCoinService;

    @PostMapping("/{videoId}/{coin}")
    @RepeatSubmit(interval = 100)
    public void payCoin(@PathVariable Long videoId, @PathVariable @Range(min = 1, max = 2) Integer coin) {
        videoCoinService.create(videoId, coin);
    }
}