package pers.tgl.mikufans.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.system.SysRole;

@Mapper
@InterceptorIgnore(dataPermission = "true")
public interface SysRoleMapper extends MPJBaseMapper<SysRole> {
}
