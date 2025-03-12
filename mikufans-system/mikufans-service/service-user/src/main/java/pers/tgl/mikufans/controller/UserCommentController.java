package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.aop.throttle.Throttle;
import pers.tgl.mikufans.domain.enums.CommentMsgType;
import pers.tgl.mikufans.dto.UserCommentDto;
import pers.tgl.mikufans.params.UserCommentParams;
import pers.tgl.mikufans.service.UserCommentMsgService;
import pers.tgl.mikufans.service.UserCommentService;
import pers.tgl.mikufans.vo.UserCommentMsgVo;
import pers.tgl.mikufans.vo.UserCommentVo;

import javax.validation.constraints.Size;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user/comment")
@Validated
@RequiredArgsConstructor
public class UserCommentController extends BaseController {
    private final UserCommentService userCommentService;
    private final UserCommentMsgService userCommentMsgService;

    /**
     * 获取指定评论
     */
    @AppLog("获取评论")
    @GetMapping("/{id}")
    public UserCommentVo getById(@PathVariable Long id) {
        return userCommentService.getVoById(id);
    }
    /**
     * 获取一组评论信息
     */
    @AppLog("获取一组评论")
    @GetMapping("/list")
    public List<UserCommentVo> getByIds(@RequestParam @Size(max = 20) List<Long> ids) {
        List<UserCommentVo> list = new ArrayList<>(ids.size());
        for (Long id : ids) {
            list.add(userCommentService.getVoById(id));
        }
        return list;
    }
    /**
     * 获取评论树
     */
    @GetMapping("/tree/{id}")
    public UserCommentVo getTreeById(@PathVariable Long id) {
        return userCommentService.getTreeById(id);
    }
    /**
     * 根据条件查找评论
     */
    @AppLog("搜索评论")
    @GetMapping
    public IPage<UserCommentVo> search(@Validated UserCommentParams search) {
        return userCommentService.search(search);
    }
    /**
     * 发送评论
     */
    @AppLog("发表评论")
    @Throttle(limit = 5, duration = 1, timeUnit = ChronoUnit.MINUTES)
    @RepeatSubmit(interval = 1000)
    @PostMapping
    public UserCommentVo create(@RequestBody @Validated UserCommentDto dto) {
        return userCommentService.create(dto);
    }
    /**
     * 删除评论
     */
    @AppLog("删除评论")
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return userCommentService.removeById(id);
    }
    /**
     * 设置评论置顶
     */
    @AppLog("评论置顶")
    @PutMapping("/top/{id}")
    public void setTop(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean top) {
        userCommentService.setTop(id, top ? 1 : 0);
    }
    /**
     * 设置评论精选
     */
    @AppLog("评论精选")
    @PutMapping("/select/{id}")
    public void setSelected(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean selected) {
        userCommentService.setSelected(id, selected ? 1 : 0);
    }
    /**
     * 消息列表
     */
    @GetMapping("/msg")
    public IPage<UserCommentMsgVo> search(@RequestParam CommentMsgType msgType) {
        return userCommentMsgService.search(msgType);
    }
}