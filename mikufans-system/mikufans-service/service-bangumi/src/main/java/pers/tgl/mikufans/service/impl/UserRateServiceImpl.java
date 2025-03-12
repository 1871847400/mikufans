package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.dto.UserRateDto;
import pers.tgl.mikufans.event.EventAction;
import pers.tgl.mikufans.event.UserDynamicEvent;
import pers.tgl.mikufans.event.UserRateEvent;
import pers.tgl.mikufans.mapper.UserRateMapper;
import pers.tgl.mikufans.model.BigDecimalWrapper;
import pers.tgl.mikufans.service.BangumiService;
import pers.tgl.mikufans.service.UserDynamicService;
import pers.tgl.mikufans.service.UserRateService;
import pers.tgl.mikufans.util.RedisUtils;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.vo.UserRateVo;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserRateServiceImpl extends BaseServiceImpl<UserRate, UserRateMapper> implements UserRateService {
    private static final String REDIS_KEY = "task:rate";

    private final RedisUtils redisUtils;
    private final UserDynamicService userDynamicService;
    private final BangumiService bangumiService;

    @Override
    public IPage<UserRateVo> search(Long targetId) {
        return wrapper()
                .eq(UserRate::getTargetId, targetId)
                .orderByDesc(UserRate::getUpdateTime)
                .page(BaseController.createPage(), UserRateVo.class)
                .fill(this::fillDefaultProperties);
    }

    @Override
    public UserRateVo getVoById(Long id) {
        UserRateVo userRate = getById(id, UserRateVo.class);
        if (userRate != null) {
            fillDefaultProperties(userRate);
        }
        return userRate;
    }

    @Override
    public UserRate getRate(Long targetId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        return wrapper().eq(UserRate::getTargetId, targetId)
                .eq(UserRate::getUserId, contextUserId)
                .one();
    }

    private void fillDefaultProperties(UserRateVo vo) {
        vo.setDynamic(userDynamicService.getVoById(vo.getId()));
        vo.setBangumi(bangumiService.getVoById(vo.getTargetId()));
    }

    @Override
    public UserRate saveRate(UserRateDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        UserRate userRate = wrapper().select(UserRate::getId)
                .eq(UserRate::getTargetId, dto.getTargetId())
                .eq(UserRate::getUserId, contextUserId)
                .one(UserRate.class);
        if (userRate == null) {
            userRate = BeanUtil.toBean(dto, UserRate.class);
            if (save(userRate)) {
                userDynamicService.createDynamic(BusinessType.RATE, userRate.getId(), null);
                publishEvent(new UserRateEvent(this, userRate, EventAction.INSERT));
            }
        } else {
            userRate.setRate(dto.getRate());
            userRate.setContent(dto.getContent());
            if (updateById(userRate)) {
                publishEvent(new UserRateEvent(this, userRate, EventAction.UPDATE));
            }
        }
        redisUtils.sadd(REDIS_KEY, dto.getTargetId());
        return userRate;
    }

    @Override
    public boolean removeById(UserRate entity) {
        if (super.removeById(entity)) {
            userDynamicService.removeById(entity.getId());
            publishEvent(new UserRateEvent(this, entity, EventAction.DELETE));
            return true;
        }
        return false;
    }

    @EventListener(UserDynamicEvent.class)
    public void listen(UserDynamicEvent e) {
        if (e.isDelete() && e.getEntity().getDynamicType() == BusinessType.RATE) {
            removeById(e.getEntity().getTargetId());
        }
    }

    /**
     * 定时计算综合评分
     */
    @Scheduled(fixedDelay = 10, initialDelay = 10, timeUnit = TimeUnit.SECONDS)
    public void calcRate() {
        Long size = redisUtils.scard(REDIS_KEY);
        if (size != null && size > 0) {
            Set<Object> ids = redisUtils.smemebers(REDIS_KEY);
            redisUtils.srem(REDIS_KEY, ids);
            for (Object id : ids) {
                BigDecimalWrapper one = wrapper()
                        .selectAvg(UserRate::getRate, BigDecimalWrapper::getValue)
                        .eq(UserRate::getTargetId, id)
                        .one(BigDecimalWrapper.class);
                if (one != null) {
                    Bangumi update = new Bangumi();
                    update.setId(Long.valueOf(id.toString()));
                    update.setRate(one.getValue());
                    Db.updateById(update);
                }
            }
        }
    }
}