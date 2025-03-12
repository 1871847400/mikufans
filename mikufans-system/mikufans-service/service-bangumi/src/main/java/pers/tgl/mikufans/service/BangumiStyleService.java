package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.bangumi.BangumiStyle;

import java.util.List;

public interface BangumiStyleService extends BaseService<BangumiStyle> {
    /**
     * 只获取id和风格名称
     */
    List<BangumiStyle> getStyles(Long bid);
    /**
     * 保存风格列表,会覆盖旧的
     */
    void saveStyles(Long bid, List<Long> styleIds);
}