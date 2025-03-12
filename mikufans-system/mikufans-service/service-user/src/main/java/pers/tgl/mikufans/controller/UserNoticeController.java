package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.service.UserNoticeService;
import pers.tgl.mikufans.vo.UserNoticeVo;

@RestController
@RequestMapping("/user/notice")
@Validated
@RequiredArgsConstructor
public class UserNoticeController extends BaseController {
    private final UserNoticeService userNoticeService;
    /**
     * 搜索自己的通知列表
     */
    @GetMapping
    public IPage<UserNoticeVo> search() {
        return userNoticeService.search();
    }
    /**
     * 隐藏通知
     */
    @AppLog("隐藏通知")
    @PutMapping("/hidden/{id}")
    public void hidden(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean hidden) {
        userNoticeService.setHidden(id, hidden ? 1 : 0);
    }
}