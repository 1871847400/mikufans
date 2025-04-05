package pers.tgl.mikufans.handler.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedJdbcTypes(JdbcType.INTEGER) //数据库类型
@MappedTypes({String.class}) //java数据类型
public class ColorTypeHandler extends BaseTypeHandler<String> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, Integer.parseInt(parameter.replace("#", ""), 16));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return "#" + Integer.toHexString(rs.getInt(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return "#" + Integer.toHexString(rs.getInt(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return "#" + Integer.toHexString(cs.getInt(columnIndex));
    }
}