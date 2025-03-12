package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.bangumi.BangumiSubscribe;

public interface BangumiSubscribeService extends BaseService<BangumiSubscribe> {
    /**
     * 订阅番剧或电影
     */
    void subscribe(Long bangumiId);
    /**
     * 取消订阅
     */
    boolean unsubscribe(Long bangumiId);
    /**
     * 是否订阅过
     */
    boolean isSubscribed(Long bangumiId);
    /**
     * 当前用户订阅数据
     */
    BangumiSubscribe getSubscribed(Long bangumiId);
}