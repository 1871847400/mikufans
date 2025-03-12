package pers.tgl.mikufans.controller.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.entity.ServerInfo;

@Slf4j
@RestController
@RequestMapping("/admin/server")
public class ServerController {
    @PreAuthorize("hasPermission('server_info', 'select')")
    @GetMapping("/info")
    public ServerInfo info() {
        return new ServerInfo();
    }
}