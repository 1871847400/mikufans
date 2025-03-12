package pers.tgl.mikufans.controller.system;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.service.SysUserService;
import pers.tgl.mikufans.vo.SysUserVo;

@RestController
@RequestMapping("/admin/sys_user")
@RequiredArgsConstructor
public class SysUserController extends BaseController {
    private final SysUserService sysUserService;

    @GetMapping(value = { "/{id}", "/" })
    public SysUserVo getOne(@PathVariable(required = false) Long id) {
        return sysUserService.getVoById(id);
    }
}