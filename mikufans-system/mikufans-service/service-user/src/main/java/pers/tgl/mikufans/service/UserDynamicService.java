package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.dto.UserDynamicDto;
import pers.tgl.mikufans.dto.UserDynamicShareDto;
import pers.tgl.mikufans.params.UserDynamicParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.UserDynamicVo;

public interface UserDynamicService extends BaseService<UserDynamic> {
    /**
     * 创建动态
     */
    UserDynamic createDynamic(BusinessType businessType, Long businessId, @Nullable UserDynamicDto dto);
    /**
     * 更新动态字段 其它业务调用
     */
    boolean updateDynamic(Long businessId, SFunction<UserDynamic, ?> col, Object val);
    /**
     * 更新动态信息
     */
    void updateDto(UserDynamicDto dto);
    /**
     * 搜索用户动态
     */
    PageImpl<UserDynamicVo> search(UserDynamicParams params);
    /**
     * 获取指定动态
     * @param id 可以是targetId,但不会是转发动态
     */
    UserDynamicVo getVoById(Object id);
    /**
     * 分享动态
     */
    UserDynamicVo createShare(UserDynamicShareDto dto);
    /**
     * 获得用户最后发送的动态
     */
    UserDynamic getLastDynamic(Long userId);
    /**
     * 获得用户未读动态数
     *   关注的所有用户从关注的时间往后的动态
     */
    int getUnread(@Nullable BusinessType dynamicType);
}