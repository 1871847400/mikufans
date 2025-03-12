package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.user.UserRate;
import pers.tgl.mikufans.dto.UserRateDto;
import pers.tgl.mikufans.service.UserRateService;
import pers.tgl.mikufans.vo.UserRateVo;

@RestController
@RequestMapping("/user/rate")
@RequiredArgsConstructor
public class UserRateController extends BaseController {
    private final UserRateService userRateService;
    /**
     * 搜索点评信息
     */
    @AppLog("搜索点评信息")
    @GetMapping("/search/{id}")
    public IPage<UserRateVo> search(@PathVariable Long id) {
        return userRateService.search(id);
    }
    /**
     *
     */
    @GetMapping("/{id}")
    public UserRateVo get(@PathVariable Long id) {
        return userRateService.getVoById(id);
    }
    /**
     * 用户点评
     * 创建和更新共用
     */
    @AppLog("用户点评")
    @PostMapping
    public UserRate save(@RequestBody @Validated UserRateDto dto) {
        return userRateService.saveRate(dto);
    }
}