package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.user.UserAlbum;
import pers.tgl.mikufans.dto.UserAlbumDto;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserAlbumParams;

public interface UserAlbumService extends BaseService<UserAlbum> {
    IPage<UserAlbum> search(UserAlbumParams search);

    UserAlbum createUserAlbum(UserAlbumDto dto);

    void updateUserAlbum(UserAlbumDto dto);

    void changeOrder(OrderDto dto);
}