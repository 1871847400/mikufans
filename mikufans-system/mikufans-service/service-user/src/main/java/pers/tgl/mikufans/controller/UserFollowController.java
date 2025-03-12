package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.consts.UserFollowStatus;
import pers.tgl.mikufans.params.UserFollowParams;
import pers.tgl.mikufans.service.UserFollowService;
import pers.tgl.mikufans.vo.UserFollowVo;

@RestController
@RequestMapping("/user/follow")
@RequiredArgsConstructor
public class UserFollowController extends BaseController {
    private final UserFollowService userFollowService;
    /**
     * 自己是否关注目标用户
     */
    @GetMapping("/peek/{targetId}")
    public UserFollowStatus getFollow(@PathVariable Long targetId) {
        return userFollowService.getFollowStatus(targetId);
    }
    /**
     * 用户关注某人
     */
    @AppLog("关注用户")
    @PostMapping("/{targetId}")
    @RepeatSubmit(interval = 500)
    public UserFollowStatus create(@PathVariable Long targetId) {
        return userFollowService.createFollow(targetId);
    }
    /**
     * 用户取消关注某人
     */
    @AppLog("取消关注")
    @DeleteMapping("/{targetId}")
    public void delete(@PathVariable Long targetId) {
        userFollowService.cancelFollow(targetId);
    }
    /**
     * 关注的用户列表
     */
    @GetMapping("/list")
    public Page<UserFollowVo> list(@Validated UserFollowParams params) {
        return userFollowService.getFollows(params);
    }
    /**
     * 粉丝列表
     */
    @GetMapping("/fans")
    public IPage<UserFollowVo> list(@RequestParam(required = false) Long userId) {
        return userFollowService.getFans(userId);
    }
}