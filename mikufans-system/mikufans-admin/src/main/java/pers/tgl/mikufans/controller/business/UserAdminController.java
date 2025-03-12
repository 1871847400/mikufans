package pers.tgl.mikufans.controller.business;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.service.UserService;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserAdminController extends BaseController {
    private final UserService userService;
}