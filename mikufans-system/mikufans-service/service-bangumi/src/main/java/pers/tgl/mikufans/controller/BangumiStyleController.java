package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.bangumi.BangumiStyle;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.service.BangumiStyleService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/bangumi/style")
@RequiredArgsConstructor
@Validated
public class BangumiStyleController extends BaseController {
    private final BangumiStyleService bangumiStyleService;

    @GetMapping("/list")
    public List<BangumiStyle> list(@RequestParam VideoType type, @RequestParam(required = false) @Positive Integer size) {
        return bangumiStyleService.wrapper()
                .eq(BangumiStyle::getVideoType, type)
                .last(size != null, "limit " + size)
                .list();
    }
}