package pers.tgl.mikufans.handler;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import pers.tgl.mikufans.util.JsonUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 自定义类型转换器,需要在配置文件中开启mybatis的扫描以注册
 *
 * 注意使用JsonNode可以同时兼容Object和Array
 */
public class JsonTypeHandler extends BaseTypeHandler<JsonNode> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return StrUtil.isNotBlank(str) ? JsonUtils.readTree(str) : null;
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return StrUtil.isNotBlank(str) ? JsonUtils.readTree(str) : null;
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return StrUtil.isNotBlank(str) ? JsonUtils.readTree(str) : null;
    }
}