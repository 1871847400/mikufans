package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserLikeData;
import pers.tgl.mikufans.model.SearchParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.LikeStatus;
import pers.tgl.mikufans.vo.UserLikeDataVo;

public interface UserLikeDataService extends BaseService<UserLikeData> {
    /**
     * 创建新的点赞数据
     */
    Long createLikeData(BusinessType likeType, Long businessId);
    /**
     * 获取点赞数据
     * @param id 也可以是业务id
     */
    UserLikeDataVo getLikeData(Long id);
    /**
     * 获取简单版点赞数据,如果不存在则返回默认数据
     * @param id 也可以是业务id
     */
    LikeStatus getStatus(Long id);
    /**
     * 搜索点赞列表,未读的优先显示
     */
    PageImpl<UserLikeDataVo> search(SearchParams params);
}