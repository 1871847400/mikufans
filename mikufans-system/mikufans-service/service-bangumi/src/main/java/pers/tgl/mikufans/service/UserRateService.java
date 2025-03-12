package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.dto.UserRateDto;
import pers.tgl.mikufans.vo.UserRateVo;

public interface UserRateService extends BaseService<UserRate> {
    /**
     * 搜索点评列表
     */
    IPage<UserRateVo> search(Long targetId);
    /**
     * 获取指定点评
     */
    UserRateVo getVoById(Long id);
    /**
     * 获取当前用户点评
     */
    UserRate getRate(Long targetId);
    /**
     * 保存用户点评
     */
    UserRate saveRate(UserRateDto dto);
}