package pers.tgl.mikufans.vo;

import lombok.Data;

import java.io.Serializable;

@Deprecated
@Data
public class AtUser implements Serializable {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 现在的用户昵称
     */
    private String nickname;
    /**
     * AT该用户时使用的昵称
     */
    private String atNickname;
}
