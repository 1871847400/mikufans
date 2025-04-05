package pers.tgl.mikufans.service;

import org.springframework.lang.Nullable;
import pers.tgl.mikufans.consts.ExpSource;
import pers.tgl.mikufans.domain.user.User;
import pers.tgl.mikufans.dto.UserDto;
import pers.tgl.mikufans.es.UserDoc;
import pers.tgl.mikufans.params.UserSearchParams;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.UserVo;

import java.util.List;

/**
* @author TGL
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2022-12-31 10:17:31
*/
public interface UserService extends BaseService<User> {
    UserVo getVoById(Long userId);
    /**
     * 根据用户名获取userId
     */
    @Nullable
    Long getUserIdByName(String username);
    /**
     * 根据nickname获取username
     */
    @Nullable
    String getUsernameByNick(String nickname);
    /**
     * 获取指定用户的等级,默认当前用户,如果没找到返回默认等级0
     */
    int getUserLevel(@Nullable Long userId);
    /**
     * 增加经验值并重新计算用户当前等级
     */
    void giveExp(Long userId, int exp, @Nullable ExpSource expSource);
    /**
     * 分页查找用户列表
     */
    PageImpl<UserVo> search(UserSearchParams params);
    /**
     * 获取补全提示
     */
    List<UserDoc> getAutoComplete(String keyword);
    /**
     * 创建一名用户
     * @param username 用户名(一般为邮箱,不可重复)
     * @param nickname 昵称(不可重复)
     * @param password 原始密码
     */
    User createUser(String username, String nickname, String password);
    /**
     * 修改用户数据
     */
    boolean updateUser(UserDto dto);
    /**
     * 更新用户头像
     * todo
     */
    void updateAvatar(String avatarUrl);
}