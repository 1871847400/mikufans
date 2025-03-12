package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.service.BangumiSubscribeService;

@RestController
@RequestMapping("/bangumi/subscribe")
@RequiredArgsConstructor
public class BangumiSubscribeController extends BaseController {
    private final BangumiSubscribeService subscribeService;

    @GetMapping("/{bangumiId}")
    public boolean query(@PathVariable Long bangumiId) {
        return subscribeService.isSubscribed(bangumiId);
    }

    @PostMapping("/{bangumiId}")
    public void create(@PathVariable Long bangumiId) {
        subscribeService.subscribe(bangumiId);
    }

    @DeleteMapping("/{bangumiId}")
    public boolean remove(@PathVariable Long bangumiId) {
        return subscribeService.unsubscribe(bangumiId);
    }
}