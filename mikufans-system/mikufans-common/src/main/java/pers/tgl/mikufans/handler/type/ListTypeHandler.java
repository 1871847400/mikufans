package pers.tgl.mikufans.handler.type;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mybatis-plus会自动扫描包来加载类型转换器
 *      该转换器会自动将 字符串(varchar) 与 List 互相转换
 * 注意：无法知道反序列化时的List泛型
 * @deprecated 使用 JacksonTypeHandler 代替
 */
@MappedJdbcTypes(JdbcType.VARCHAR) //数据库类型
@MappedTypes({List.class}) //java数据类型
@Deprecated
public class ListTypeHandler extends BaseTypeHandler<List<Object>> {
    private static final String split = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Object> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, StrUtil.join(split, parameter));
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return handle(rs.getString(columnName));
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return handle(rs.getString(columnIndex));
    }

    @Override
    public List<Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return handle(cs.getString(columnIndex));
    }

    private List<Object> handle(String str) {
        return StrUtil.isNotBlank(str) ? ListUtil.toList(str.split(split))
                .stream()
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList()) : ListUtil.empty();
    }
}