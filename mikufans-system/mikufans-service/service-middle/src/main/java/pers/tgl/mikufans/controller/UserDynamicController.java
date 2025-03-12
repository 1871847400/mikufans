package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.enums.BusinessType;
import pers.tgl.mikufans.domain.user.UserDynamic;
import pers.tgl.mikufans.dto.UserDynamicShareDto;
import pers.tgl.mikufans.params.UserDynamicParams;
import pers.tgl.mikufans.service.UserDynamicService;
import pers.tgl.mikufans.service.UserPublishService;
import pers.tgl.mikufans.service.UserRateService;
import pers.tgl.mikufans.service.VideoService;
import pers.tgl.mikufans.vo.UserDynamicVo;

/**
 * 充当动态和其他业务的中间层
 */
@RestController
@RequestMapping("/user/dynamic")
@RequiredArgsConstructor
public class UserDynamicController extends BaseController {
    private final UserDynamicService userDynamicService;
    private final VideoService videoService;
    private final UserPublishService userPublishService;
    private final UserRateService userRateService;
    /**
     * 搜素动态
     */
    @GetMapping
    public IPage<? extends UserDynamic> search(@Validated UserDynamicParams search) {
        return userDynamicService.search(search).convert(this::fill);
    }
    /**
     * 搜索指定动态
     */
    @GetMapping("/{id}")
    public UserDynamicVo getById(@PathVariable Long id) {
        return fill(userDynamicService.getVoById(id));
    }
    /**
     * 获取未读动态数量
     */
    @GetMapping("/unread")
    public int getUnread(@RequestParam(required = false) BusinessType type) {
        return userDynamicService.getUnread(type);
    }
    /**
     * 转发动态
     */
    @AppLog("转发动态")
    @PostMapping("/share")
    public UserDynamicVo share(@RequestBody @Validated UserDynamicShareDto dto) {
        return fill(userDynamicService.createShare(dto));
    }
    /**
     * 删除动态
     */
    @AppLog("删除动态")
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return userDynamicService.removeById(id);
    }

    private UserDynamicVo fill(UserDynamicVo vo) {
        if (vo != null) {
            switch (vo.getDynamicType()) {
                case VIDEO:
                    vo.setSource(videoService.getVoById(vo.getTargetId()));
                    break;
                case PUBLISH:
                    vo.setSource(userPublishService.getById(vo.getTargetId()));
                    break;
                case RATE:
                    vo.setSource(userRateService.getVoById(vo.getTargetId()));
                    break;
            }
        }
        return vo;
    }
}