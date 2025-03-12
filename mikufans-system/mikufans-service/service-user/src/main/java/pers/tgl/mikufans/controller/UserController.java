package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.consts.UserSearchSort;
import pers.tgl.mikufans.dto.UserDto;
import pers.tgl.mikufans.es.UserDoc;
import pers.tgl.mikufans.model.QueryOption;
import pers.tgl.mikufans.params.UserSearchParams;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.util.SecurityUtils;
import pers.tgl.mikufans.validator.group.Update;
import pers.tgl.mikufans.vo.UserVo;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final UserService userService;
    /**
     * 获取指定id的用户资料
     * / or /1 必须带杠
     */
    @GetMapping(value = { "", "/", "/{userId}" })
    public UserVo get(@PathVariable(required = false) Long userId) {
        if (userId == null) {
            userId = SecurityUtils.getContextUserId(false);
            if (userId == null) {
                return null;
            }
        }
        return userService.getVoById(userId);
    }
    /**
     * 按条件查找用户列表
     */
    @GetMapping("/search")
    public IPage<UserVo> search(@Validated UserSearchParams params) {
        return userService.search(params);
    }
    /**
     * 修改用户的资料
     */
    @AppLog("修改用户资料")
    @PutMapping
    public Boolean update(@RequestBody @Validated(Update.class) UserDto dto) {
        return userService.updateUser(dto);
    }
    /**
     * 搜索昵称关键词补全
     */
    @GetMapping("/auto_complete")
    public List<UserDoc> getAutoComplete(@RequestParam String keyword) {
        return userService.getAutoComplete(keyword);
    }
    /**
     * 搜索筛选项
     */
    @GetMapping("/search/options")
    @Cacheable(cacheNames = "user-search-options")
    public List<QueryOption> getSearchOptions() {
        List<QueryOption> options = new ArrayList<>();
        QueryOption sort = new QueryOption("sort", UserSearchSort.DEFAULT.name());
        for (UserSearchSort value : UserSearchSort.values()) {
            sort.addOption(value.getLabel(), value.name());
        }
        options.add(sort);
        return options;
    }
}