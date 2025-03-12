package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.bangumi.BangumiStyleLink;
import pers.tgl.mikufans.domain.bangumi.BangumiSubscribe;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.domain.video.Video;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.domain.video.VideoWatchHistory;
import pers.tgl.mikufans.dto.BangumiDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserRateEvent;
import pers.tgl.mikufans.mapper.BangumiMapper;
import pers.tgl.mikufans.params.BangumiParams;
import pers.tgl.mikufans.service.*;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.BangumiSeries;
import pers.tgl.mikufans.vo.BangumiVo;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BangumiServiceImpl extends BaseServiceImpl<Bangumi, BangumiMapper> implements BangumiService {
    private final BangumiStyleService bangumiStyleService;
    private final UserFlagService userFlagService;

    @Lazy
    @Resource
    private UserRateService userRateService;
    @Resource
    @Lazy
    private BangumiSubscribeService subscribeService;

    @Override
    public BangumiVo getVoById(Long id) {
        BangumiVo vo = getById(id, BangumiVo.class);
        if (vo != null) {
            vo.setVideo(Db.getById(vo.getVideoId(), Video.class));
            vo.setSubscribed(subscribeService.getSubscribed(id));
            vo.setUserRate(userRateService.getRate(id));
            vo.setStyles(bangumiStyleService.getStyles(id));
            vo.setSeries(getSeriesList(vo.getSeriesIds()));
            vo.setDesc(getDesc(vo.getVideo().getId(), vo.getReleaseStatus()));
        }
        return vo;
    }

    private List<BangumiSeries> getSeriesList(List<String> ids) {
        List<BangumiSeries> seriesList = new ArrayList<>();
        if (CollUtil.isNotEmpty(ids)) {
            for (String bid : ids) {
                Bangumi bangumi = getById(bid);
                if (bangumi == null) {
                    continue;
                }
                Video video = Db.getById(bangumi.getVideoId(), Video.class);
                if (video == null) {
                    continue;
                }
                BangumiSeries series = new BangumiSeries();
                series.setBid(bangumi.getId());
                series.setSeriesTag(bangumi.getSeriesTag());
                series.setVid(video.getId());
                series.setSid(video.getSid());
                seriesList.add(series);
            }
        }
        return seriesList;
    }

    @Override
    public PageImpl<BangumiVo> search(BangumiParams params) {
        if (userFlagService.isDeny(params.getSubscribedUserId(), UserFlags.PUBLIC_SUBSCRIBE)) {
            return PageImpl.empty();
        }
        final String alias = "video";
        MPJLambdaWrapper<Bangumi> wrapper = wrapper()
                .innerJoin(Video.class, alias, Video::getBangumiId, Bangumi::getId)
                .selectAll(Bangumi.class)
                .selectAssociation(Video.class, BangumiVo::getVideo)
                //可被搜索
                .eq(Video::getSearch, 1)
                //限定类型
                .eqIfExists(Video::getType, params.getVideoType())
                //限定播放年份
                .apply(params.getYear() != null, "YEAR(premiere) = {0}", params.getYear())
                //限定更新状态
                .eqIfExists(Bangumi::getReleaseStatus, params.getStatus())
                //限定季度
                .eqIfExists(Bangumi::getReleaseSeason, params.getSeason())
                //限定星期
                .eqIfExists(Bangumi::getReleaseWeek, params.getWeek())
                //限定地区
                .eqIfExists(Bangumi::getRegionId, params.getRegion());

        //进入这里才能执行下面的根据订阅时间排序
        if (isValidId(params.getSubscribedUserId())) {
            //只查找订阅了的视频,所以用内连接
            wrapper.selectAs(BangumiSubscribe::getCreateTime, "subscribe_time")
                    .innerJoin(BangumiSubscribe.class, w ->
                            w.eq(BangumiSubscribe::getBangumiId, Bangumi::getId)
                                    .eq(BangumiSubscribe::getUserId, params.getSubscribedUserId())
                    );
        }
        //限定风格
        if (params.getStyle() != null) {
            wrapper.innerJoin(BangumiStyleLink.class, BangumiStyleLink::getBid, Bangumi::getId)
                    .eq(BangumiStyleLink::getSid, params.getStyle());
        }
        //关键字搜索
        if (StrUtil.isNotBlank(params.getTitle())) {
            //布尔模式: +必须包含
            String col = alias + "." + pers.tgl.mikufans.domain.video.Video.Fields.title;
            String sql = String.format("match(%s) against ('+%s' in boolean mode)", col, params.getTitle().trim());
            wrapper.apply(sql);
        }

        switch (params.getSort()) {
            case rate:
                wrapper.orderBy(true, params.isAsc(), Bangumi::getRate);
                break;
            case premiere:
                wrapper.orderBy(true, params.isAsc(), Bangumi::getPremiere);
                break;
            case random:
                wrapper.orderBy(true, params.isAsc(), "rand()");
                break;
            case subscribe_time:
                if (isValidId(params.getSubscribedUserId())) {
                    wrapper.orderBy(true, params.isAsc(), BangumiSubscribe::getCreateTime);
                }
                break;
            case subscribe:
                wrapper.orderBy(true, params.isAsc(), Bangumi::getSubscribe);
                break;
            case play:
                wrapper.orderBy(true, params.isAsc(), Video::getPlays);
                break;
        }
        return wrapper.page(params.page(), BangumiVo.class).fill(vo -> {
            Long vid = vo.getVideo().getId();
            vo.setSubscribed(subscribeService.getSubscribed(vo.getId()));
            vo.setUserRate(userRateService.getRate(vo.getId()));
            vo.setDesc(getDesc(vid, vo.getReleaseStatus()));
            vo.setStyles(bangumiStyleService.getStyles(vo.getId()));
            VideoWatchHistory history = Db.lambdaQuery(VideoWatchHistory.class)
                    .eq(VideoWatchHistory::getVideoId, vid)
                    .eq(VideoWatchHistory::getUserId, SecurityUtils.getContextUserId(false))
                    .one();
            vo.setWatchInfo(getWatchInfo(history));
            vo.setWatchProgress(getWatchProgress(history));
        });
    }

    private String getWatchInfo(VideoWatchHistory history) {
        if (history == null) {
            return "尚未观看";
        }
        VideoPart part = Db.getById(history.getPartId(), VideoPart.class);
        if (part == null) {
            return "观看过";
        }
        String time = MyUtils.formatDuration(Duration.ofMillis(history.getWatchPos()));
        return "看到第" + part.getSort() + "集 " + time;
    }

    private String getDesc(Long videoId, Integer status) {
        Long count = Db.lambdaQuery(VideoPart.class).eq(VideoPart::getVideoId, videoId)
                .eq(VideoPart::getCanplay, 1)
                .count();
        if (status == 1) {
            return "更新中,第" + count + "集";
        } else if (status == 2) {
            return "已完结,共" + count + "集";
        } else {
            return "";
        }
    }

    private Float getWatchProgress(VideoWatchHistory history) {
        if (history == null) {
            return 0F;
        }
        VideoPart part = Db.getById(history.getPartId(), VideoPart.class);
        if (part == null) {
            return 0F;
        }
        VideoResource res = Db.getById(part.getResId(), VideoResource.class);
        if (res == null) {
            return 0F;
        }
        return history.getWatchPos() / (float) res.getDuration();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBangumi(BangumiDto dto) {
        Bangumi bangumi = BeanUtil.toBean(dto, Bangumi.class);
        save(bangumi);
        bangumiStyleService.saveStyles(bangumi.getId(), dto.getStyles());
        return bangumi.getId();
    }

    @Override
    public boolean updateBangumi(BangumiDto dto) {
        dto.setVideoId(null);
        bangumiStyleService.saveStyles(dto.getId(), dto.getStyles());
        return updateById(BeanUtil.toBean(dto, Bangumi.class));
    }

    @Override
    public void addSubscribe(Long id, int count) {
        incrementById(id, Bangumi::getSubscribe, count);
    }

    @EventListener(UserRateEvent.class)
    public void listen(UserRateEvent e) {
        Long targetId = e.getEntity().getTargetId();
        if (e.getAction() == EventAction.INSERT) {
            incrementById(targetId, Bangumi::getRateCount, 1);
        } else if (e.getAction() == EventAction.DELETE) {
            incrementById(targetId, Bangumi::getRateCount, -1);
        }
    }
}