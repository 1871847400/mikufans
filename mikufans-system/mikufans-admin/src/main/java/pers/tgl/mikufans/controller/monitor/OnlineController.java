package pers.tgl.mikufans.controller.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.entity.OnlineUser;
import pers.tgl.mikufans.interceptor.OnlineInterceptor;
import pers.tgl.mikufans.util.PageImpl;

@Slf4j
@RestController
@RequestMapping("/admin/online")
@RequiredArgsConstructor
public class OnlineController extends BaseController {
    private final OnlineInterceptor onlineInterceptor;

    @PreAuthorize("hasPermission('online_user', 'select')")
    @GetMapping
    public IPage<OnlineUser> getOnlineUsers() {
        PageImpl<OnlineUser> page = createPage();
        page.setRecords(onlineInterceptor.getOnlineUsers(page.getCurrent(), page.getSize()));
        page.setTotal(onlineInterceptor.getOnlineUserCount());
        return page;
    }
}