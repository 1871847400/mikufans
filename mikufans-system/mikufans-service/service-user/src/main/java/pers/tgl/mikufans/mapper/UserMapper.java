package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.user.User;

/**
* @author TGL
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2022-12-31 10:17:31
* @Entity .domain.User
*/
@Mapper
public interface UserMapper extends MPJBaseMapper<User> {

}