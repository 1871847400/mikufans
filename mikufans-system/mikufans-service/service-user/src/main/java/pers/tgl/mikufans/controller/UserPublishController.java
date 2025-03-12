package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.aop.throttle.Throttle;
import pers.tgl.mikufans.domain.user.UserPublish;
import pers.tgl.mikufans.dto.UserPublishDto;
import pers.tgl.mikufans.service.UserPublishService;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/user/publish")
@RequiredArgsConstructor
public class UserPublishController extends BaseController {
    private final UserPublishService userPublishService;

    @AppLog("发表说说")
    @RepeatSubmit(interval = 2000)
    @Throttle(limit = 3, duration = 1, timeUnit = ChronoUnit.MINUTES)
    @PostMapping
    public UserPublish create(@RequestBody @Validated(Create.class) UserPublishDto dto) {
        return userPublishService.createDto(dto);
    }

    @AppLog("更新说说")
    @PutMapping
    public void update(@RequestBody @Validated(Update.class) UserPublishDto dto) {
        userPublishService.updateDto(dto);
    }

    @GetMapping("/{id}")
    public UserPublish getById(@PathVariable Long id) {
        return userPublishService.getById(id);
    }

    @AppLog("删除说说")
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return userPublishService.removeById(id);
    }
}