package pers.tgl.mikufans.entity;

import lombok.Data;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;

import java.io.Serializable;
import java.util.Date;

@Data
@PermFlag(name = "在线用户", group = PermGroup.BUSINESS)
public class OnlineUser implements Serializable {
    private Long id;
    private Integer userType;
    private String username;
    private String ipaddr;
    private String location;
    private Date time;
}