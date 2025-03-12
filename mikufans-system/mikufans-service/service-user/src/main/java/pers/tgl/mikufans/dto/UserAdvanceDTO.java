package pers.tgl.mikufans.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户高级信息(供管理员修改)
 */
@Data
@Deprecated
public class UserAdvanceDTO implements Serializable {
    private Integer level;
    private Integer coin;
    private Integer status;
    private Date registerTime;
}