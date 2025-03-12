package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.bangumi.BangumiStyle;
import pers.tgl.mikufans.domain.bangumi.BangumiStyleLink;
import pers.tgl.mikufans.mapper.BangumiStyleMapper;
import pers.tgl.mikufans.service.BangumiStyleService;

import java.util.ArrayList;
import java.util.List;

@Service
public class BangumiStyleServiceImpl extends BaseServiceImpl<BangumiStyle, BangumiStyleMapper>
    implements BangumiStyleService {

    @Override
    public List<BangumiStyle> getStyles(Long bid) {
        return wrapper()
                .select(BangumiStyle::getId, BangumiStyle::getStyleName)
                .innerJoin(BangumiStyleLink.class, BangumiStyleLink::getSid, BangumiStyle::getId)
                .eq(BangumiStyleLink::getBid, bid)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStyles(Long bid, List<Long> styleIds) {
        //删除以前存在，本次保存不在的风格
        Db.lambdaUpdate(BangumiStyleLink.class)
                .eq(BangumiStyleLink::getBid, bid)
                .notIn(CollUtil.isNotEmpty(styleIds), BangumiStyleLink::getSid, styleIds)
                .remove();
        if (CollUtil.isNotEmpty(styleIds)) {
            List<BangumiStyleLink> styleLinks = new ArrayList<>(styleIds.size());
            for (Long sid : styleIds) {
                BangumiStyleLink styleLink = new BangumiStyleLink();
                styleLink.setBid(bid);
                styleLink.setSid(sid);
                boolean exists = Db.lambdaQuery(BangumiStyleLink.class)
                        .eq(BangumiStyleLink::getBid, bid)
                        .eq(BangumiStyleLink::getSid, sid)
                        .exists();
                if (!exists) {
                    styleLinks.add(styleLink);
                }
            }
            if (!styleLinks.isEmpty()) {
                Db.saveBatch(styleLinks);
            }
        }
    }
}