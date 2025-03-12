package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.message.MsgUnread;
import pers.tgl.mikufans.service.MsgUnreadService;

@RestController
@RequestMapping("/msg")
@RequiredArgsConstructor
public class MessageController extends BaseController {
    private final MsgUnreadService msgUnreadService;
    /**
     * 获取未读消息的总数
     */
    @GetMapping("/unread")
    public MsgUnread getUnread() {
        return msgUnreadService.getByUser(null);
    }
}