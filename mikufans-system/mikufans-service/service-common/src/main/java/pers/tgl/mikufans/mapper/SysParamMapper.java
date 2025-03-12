package pers.tgl.mikufans.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.system.SysParam;

@Mapper
//@InterceptorIgnore(dataPermission = "true")
public interface SysParamMapper extends BaseMapper<SysParam> {

}