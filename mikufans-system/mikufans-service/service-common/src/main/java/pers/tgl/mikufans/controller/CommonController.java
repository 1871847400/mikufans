package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.toolkit.JoinWrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.enums.CarouselPosition;
import pers.tgl.mikufans.domain.system.SysCarousel;
import pers.tgl.mikufans.domain.system.SysRegion;
import pers.tgl.mikufans.domain.video.Video;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommonController extends BaseController {
    /**
     * 获取指定位置的轮播广告
     */
    @GetMapping("/carousel/{pos}")
    public List<SysCarousel> getCarousels(@PathVariable CarouselPosition pos,
                                          @RequestParam(required = false) Long channelId) {
        return JoinWrappers.lambda(SysCarousel.class)
                .eq(SysCarousel::getPosition, pos)
                .eq(SysCarousel::getDisabled, 0)
                .eqIfExists(SysCarousel::getChannelId, channelId)
                .and(w->w.le(SysCarousel::getStartDate, new Date()).or().isNull(SysCarousel::getStartDate))
                .and(w->w.ge(SysCarousel::getEndDate, new Date()).or().isNull(SysCarousel::getEndDate))
                .selectAssociation(Video.class, SysCarousel::getVideo)
                .leftJoin(Video.class, Video::getId, SysCarousel::getVideoId)
                .selectAssociation(Bangumi.class, SysCarousel::getBangumi)
                .leftJoin(Bangumi.class, Bangumi::getId, Video::getBangumiId)
                .orderByAsc(SysCarousel::getSort)
                .list(SysCarousel.class);
    }
    /**
     * 所有支持的地区
     */
    @GetMapping("/region")
    public List<SysRegion> listRegions() {
        return Db.list(SysRegion.class);
    }
}