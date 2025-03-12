package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.domain.user.UserStar;
import pers.tgl.mikufans.dto.UserStarDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserStarParams;
import pers.tgl.mikufans.service.UserStarService;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;
import pers.tgl.mikufans.vo.UserStarVo;

import java.util.List;

@RestController
@RequestMapping("/user/star")
@RequiredArgsConstructor
public class UserStarController extends BaseController {
    private final UserStarService userStarService;
    /**
     * 按条件查询收藏夹
     */
    @GetMapping("/search")
    public List<UserStarVo> search(@Validated UserStarParams params) {
        return userStarService.search(params);
    }
    /**
     * 创建收藏夹
     */
    @PostMapping
    @RepeatSubmit(interval = 500)
    public UserStar create(@RequestBody @Validated(Create.class) UserStarDto dto) {
        return userStarService.create(dto);
    }
    /**
     * 更新收藏夹
     */
    @PutMapping
    public boolean update(@RequestBody @Validated(Update.class) UserStarDto dto) {
        return userStarService.update(dto);
    }
    /**
     * 删除收藏夹
     */
    @DeleteMapping("/{id}")
    public Boolean remove(@PathVariable Long id) {
        return userStarService.removeById(id);
    }
    /**
     * 更改收藏夹顺序
     */
    @PutMapping("/order")
    public void order(@RequestBody @Validated OrderDto dto) {
        userStarService.changeOrder(dto);
    }
    /**
     * 是否收藏过该视频
     */
    @GetMapping("/check/{videoId}")
    public boolean check(@PathVariable Long videoId) {
        return userStarService.hasStarVideo(videoId);
    }
}