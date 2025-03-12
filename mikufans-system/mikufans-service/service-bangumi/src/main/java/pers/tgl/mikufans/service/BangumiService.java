package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.bangumi.Bangumi;
import pers.tgl.mikufans.dto.BangumiDto;
import pers.tgl.mikufans.params.BangumiParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.BangumiVo;

public interface BangumiService extends BaseService<Bangumi> {
    /**
     * 根据id获取
     */
    BangumiVo getVoById(Long id);
    /**
     * 综合搜索
     */
    PageImpl<BangumiVo> search(BangumiParams search);
    /**
     * 创建
     */
    Long createBangumi(BangumiDto dto);
    /**
     * 根据id更新
     */
    boolean updateBangumi(BangumiDto dto);
    /**
     * 增加或减少订阅次数
     */
    void addSubscribe(Long id, int count);
}