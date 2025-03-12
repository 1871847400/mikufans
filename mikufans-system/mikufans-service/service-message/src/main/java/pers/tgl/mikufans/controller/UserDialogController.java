package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.message.UserDialog;
import pers.tgl.mikufans.service.UserDialogService;
import pers.tgl.mikufans.util.SecurityUtils;

@RestController
@RequestMapping("/user/dialog")
@RequiredArgsConstructor
public class UserDialogController extends BaseController {
    private final UserDialogService userDialogService;

    @AppLog("创建会话")
    @PostMapping("/{targetId:\\d+}")
    public void create(@PathVariable Long targetId) {
        userDialogService.createDialog(targetId);
    }

    @AppLog("创建临时会话")
    @PostMapping("/temp/{targetId}")
    public UserDialog createTemp(@PathVariable Long targetId) {
        return userDialogService.createTempDialog(targetId);
    }

    @AppLog("搜索会话")
    @GetMapping("/target/{targetId}")
    public UserDialog get(@PathVariable Long targetId) {
        return userDialogService.getById(targetId);
    }

    @AppLog("删除会话")
    @DeleteMapping("/target/{targetId}")
    public void remove(@PathVariable Long targetId) {
        UserDialog userDialog = userDialogService.wrapper().eq(UserDialog::getTargetId, targetId)
                .eq(UserDialog::getUserId, SecurityUtils.getContextUserId(true))
                .one();
        userDialogService.removeById(userDialog);
    }

    @AppLog("删除会话")
    @DeleteMapping("/{id}")
    public void removeById(@PathVariable Long id) {
        userDialogService.removeById(id);
    }


    @AppLog("搜索会话列表")
    @GetMapping("/search")
    public IPage<UserDialog> search() {
        return userDialogService.search();
    }

    @AppLog("会话置顶")
    @PutMapping("/top/{id}/{top}")
    public void setTop(@PathVariable Long id, @PathVariable Integer top) {
        userDialogService.setTop(id, top > 0);
    }
}