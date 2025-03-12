package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.video.VideoChannel;
import pers.tgl.mikufans.params.VideoChannelParams;
import pers.tgl.mikufans.service.VideoChannelService;

import java.util.List;

@RestController
@RequestMapping("/video/channel")
@RequiredArgsConstructor
public class VideoChannelController extends BaseController {
    private final VideoChannelService videoChannelService;

    @GetMapping("/list")
    public List<VideoChannel> list(@Validated VideoChannelParams search) {
        return videoChannelService.search(search);
    }
}