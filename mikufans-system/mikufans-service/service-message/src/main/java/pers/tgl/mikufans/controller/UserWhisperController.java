package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.aop.throttle.Throttle;
import pers.tgl.mikufans.domain.message.UserWhisper;
import pers.tgl.mikufans.dto.UserWhisperDto;
import pers.tgl.mikufans.search.UserWhisperSearch;
import pers.tgl.mikufans.service.UserWhisperService;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/user/whisper")
@RequiredArgsConstructor
public class UserWhisperController extends BaseController {
    private final UserWhisperService userWhisperService;

    @AppLog("发送私信")
    @RepeatSubmit(interval = 100)
    @Throttle(limit = 20, duration = 1, timeUnit = ChronoUnit.MINUTES, key = "#dto.receiverId")
    @PostMapping
    public UserWhisper create(@RequestBody @Validated UserWhisperDto dto) {
        return userWhisperService.createWhisper(dto);
    }
    /**
     * 搜索和目标用户的聊天列表
     */
    @AppLog("搜索私信记录")
    @GetMapping
    public IPage<UserWhisper> search(@Validated UserWhisperSearch search) {
        return userWhisperService.search(search);
    }

    @AppLog("撤回私信")
    @PutMapping("/revoke/{id}")
    public void revoke(@PathVariable Long id) {
        userWhisperService.revokeWhisper(id);
    }
}