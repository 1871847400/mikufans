package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.consts.Period;
import pers.tgl.mikufans.domain.video.VideoCoin;

/**
* @author TGL
* @createDate 2023-01-20 16:09:02
*/
public interface VideoCoinService extends BaseService<VideoCoin> {
    /**
     * 给视频投币
     */
    VideoCoin create(Long videoId, int count);
    /**
     * 统计硬币总数
     */
    int countCoin(@Nullable Long videoId,
                  @Nullable Long userId,
                  @Nullable Period period);
    /**
     * 获取当前用户投币数量
     */
    int getUserCoin(Long videoId);
}