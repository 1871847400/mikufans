package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.user.UserAlbum;
import pers.tgl.mikufans.dto.UserAlbumDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserAlbumParams;
import pers.tgl.mikufans.service.UserAlbumService;
import pers.tgl.mikufans.validator.group.Create;
import pers.tgl.mikufans.validator.group.Update;

import java.util.List;

@RestController
@RequestMapping("/user/album")
@RequiredArgsConstructor
public class UserAlbumController extends BaseController {
    private final UserAlbumService userAlbumService;

    @AppLog("搜索相册")
    @GetMapping("/search")
    public IPage<UserAlbum> search(@Validated UserAlbumParams search) {
        return userAlbumService.search(search);
    }

    @AppLog("创建相册")
    @PostMapping
    public UserAlbum create(@RequestBody @Validated(Create.class) UserAlbumDto dto) {
        return userAlbumService.createUserAlbum(dto);
    }

    @AppLog("更新相册")
    @PutMapping
    public void update(@RequestBody @Validated(Update.class) UserAlbumDto dto) {
        userAlbumService.updateUserAlbum(dto);
    }

    @AppLog("删除相册")
    @DeleteMapping("/{ids}")
    public void delete(@PathVariable List<Long> ids) {
        ids.forEach(userAlbumService::removeById);
    }

    @AppLog("修改相册顺序")
    @PutMapping("/order")
    public void order(@RequestBody @Validated OrderDto orderDto) {
        userAlbumService.changeOrder(orderDto);
    }
}