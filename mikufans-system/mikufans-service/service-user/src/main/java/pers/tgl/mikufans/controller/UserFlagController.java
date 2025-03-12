package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.domain.enums.UserFlags;
import pers.tgl.mikufans.dto.UserFlagDto;
import pers.tgl.mikufans.service.UserFlagService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/flag")
@RequiredArgsConstructor
public class UserFlagController extends BaseController {
    private final UserFlagService userFlagService;
    /**
     * 获取标记设置
     */
    @GetMapping("/list")
    public Map<UserFlags, Map<String, Object>> getUserFlags() {
        Map<UserFlags, Map<String, Object>> result = new LinkedHashMap<>();
        for (UserFlags userFlags : UserFlags.values()) {
            result.put(userFlags, userFlags.getMap());
        }
        return result;
    }
    /**
     * 获取指定用户的标志设置
     */
    @GetMapping("/list/{userId}")
    public Map<UserFlags, String> list(@PathVariable Long userId) {
        return userFlagService.getUserFlags(userId);
    }
    /**
     * 修改标志
     */
    @PutMapping
    public void save(@RequestBody @Validated UserFlagDto dto) {
        userFlagService.saveUserFlag(dto);
    }
}