package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.dto.UserStarDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserStarParams;
import pers.tgl.mikufans.vo.UserStarVo;

import java.io.Serializable;
import java.util.List;

/**
* @author TGL
* @description 针对表【user_star(用户收藏夹表)】的数据库操作Service
* @createDate 2023-01-20 16:09:02
*/
public interface UserStarService extends BaseService<UserStar> {
    /**
     * 创建收藏夹
     */
    UserStar create(UserStarDto dto);
    /**
     * 更新收藏夹
     */
    boolean update(UserStarDto dto);
    /**
     * 按条件查找收藏夹
     */
    List<UserStarVo> search(UserStarParams params);
    /**
     * 删除收藏夹
     */
    boolean removeById(Serializable id);
    /**
     * 改变收藏夹顺序
     */
    void changeOrder(OrderDto dto);
    /**
     * 是否收藏过指定视频
     */
    boolean hasStarVideo(Long videoId);
    /**
     * 获取默认收藏夹
     */
    UserStar getDefaultUserStar();
}