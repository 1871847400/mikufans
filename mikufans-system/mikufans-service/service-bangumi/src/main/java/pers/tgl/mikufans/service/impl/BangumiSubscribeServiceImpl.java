package pers.tgl.mikufans.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.bangumi.BangumiSubscribe;
import pers.tgl.mikufans.event.SubscribeEvent;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.BangumiSubscribeMapper;
import pers.tgl.mikufans.service.BangumiService;
import pers.tgl.mikufans.service.BangumiSubscribeService;
import pers.tgl.mikufans.util.SecurityUtils;

@Service
@RequiredArgsConstructor
public class BangumiSubscribeServiceImpl extends BaseServiceImpl<BangumiSubscribe, BangumiSubscribeMapper> implements BangumiSubscribeService {
    private final BangumiService bangumiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void subscribe(Long bangumiId) {
        if (!bangumiService.exists(bangumiId)) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        BangumiSubscribe bangumiSubscribe = new BangumiSubscribe();
        bangumiSubscribe.setBangumiId(bangumiId);
        try {
            save(bangumiSubscribe);
        } catch (DuplicateKeyException ignored) {}
        bangumiService.addSubscribe(bangumiId, 1);
        publishEvent(new SubscribeEvent(this, bangumiSubscribe, false));
    }

    @Override
    public boolean unsubscribe(Long bangumiId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        BangumiSubscribe subscribe = lambdaQuery()
                .eq(BangumiSubscribe::getBangumiId, bangumiId)
                .eq(BangumiSubscribe::getUserId, contextUserId)
                .one();
        return removeById(subscribe);
    }

    @Override
    public boolean isSubscribed(Long bangumiId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        return lambdaQuery().eq(BangumiSubscribe::getUserId, contextUserId)
                .eq(BangumiSubscribe::getBangumiId, bangumiId)
                .exists();
    }

    @Override
    public BangumiSubscribe getSubscribed(Long bangumiId) {
        Long contextUserId = SecurityUtils.getContextUserId(false);
        if (contextUserId != null) {
            return Db.lambdaQuery(BangumiSubscribe.class)
                    .eq(BangumiSubscribe::getBangumiId, bangumiId)
                    .eq(BangumiSubscribe::getUserId, contextUserId)
                    .one();
        }
        return null;
    }

    @Override
    public boolean removeById(BangumiSubscribe entity) {
        if (super.removeById(entity)) {
            bangumiService.addSubscribe(entity.getBangumiId(), -1);
            publishEvent(new SubscribeEvent(this, entity, true));
            return true;
        }
        return false;
    }
}