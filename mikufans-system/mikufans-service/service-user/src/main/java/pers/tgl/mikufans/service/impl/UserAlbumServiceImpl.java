package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.tgl.mikufans.domain.user.UserAlbum;
import pers.tgl.mikufans.dto.UserAlbumDto;
import pers.tgl.mikufans.mapper.UserAlbumMapper;
import pers.tgl.mikufans.model.OrderDto;
import pers.tgl.mikufans.params.UserAlbumParams;
import pers.tgl.mikufans.service.UserAlbumService;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.util.SecurityUtils;

@Service
@RequiredArgsConstructor
public class UserAlbumServiceImpl extends BaseServiceImpl<UserAlbum, UserAlbumMapper> implements UserAlbumService {
    private final UserLikeDataService userLikeDataService;

    @Override
    public IPage<UserAlbum> search(UserAlbumParams params) {
        if (params.getUserId() == null) {
            params.setUserId(SecurityUtils.getContextUserId(true));
        }
        return wrapper()
                .eq(UserAlbum::getUserId, params.getUserId())
                .eq(UserAlbum::getPid, params.getPid())
                .orderByDesc(UserAlbum::getCategory)
                .orderByAsc(UserAlbum::getSort)
                .orderByDesc(UserAlbum::getCreateTime)
                .page(params.page(), UserAlbum.class).fill(userAlbum -> {});
    }

    @Override
    public UserAlbum createUserAlbum(UserAlbumDto dto) {
        UserAlbum userAlbum = BeanUtil.toBean(dto, UserAlbum.class);
        userAlbum.setSort(1);
        if (userAlbum.getPid() == null) {
            userAlbum.setPid(0L);
        }
        save(userAlbum);
        return userAlbum;
    }

    @Override
    public void updateUserAlbum(UserAlbumDto dto) {
        updateById(BeanUtil.toBean(dto, UserAlbum.class));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeOrder(OrderDto dto) {
        if (CollUtil.isEmpty(dto.getIds())) {
            return;
        }
        Long contextUserId = SecurityUtils.getContextUserId(true);
        int i = 1;
        for (Long id : dto.getIds()) {
            boolean update = lambdaUpdate().eq(UserAlbum::getId, id)
                    .eq(UserAlbum::getUserId, contextUserId)
                    .set(UserAlbum::getSort, i)
                    .update();
            if (update) {
                i++;
            }
        }
    }
}