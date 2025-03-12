package pers.tgl.mikufans.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.system.SysPerm;
import pers.tgl.mikufans.domain.system.SysRole;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleVO extends SysRole {
    private List<SysPerm> authorities;
}