package pers.tgl.mikufans.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 作为token的body使用,数据不能过多以免超过header大小,并且属性不能是经常变化的
 *  注意body内容为base64明文，不能存储敏感信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserToken implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户名(邮箱)
     */
    private String username;
    /**
     * 密码版本,当用户修改密码后token失效
     */
    private Integer version;
    /**
     * 用户类型 0:普通 1:管理员
     */
    private Integer userType;

    @JsonIgnore
    public boolean isAdmin() {
        return Objects.equals(userType, 1);
    }
}