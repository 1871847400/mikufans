package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.domain.user.UserLike;
import pers.tgl.mikufans.model.SearchParams;
import pers.tgl.mikufans.service.UserLikeDataService;
import pers.tgl.mikufans.service.UserLikeService;
import pers.tgl.mikufans.util.PageImpl;
import pers.tgl.mikufans.vo.LikeStatus;
import pers.tgl.mikufans.vo.UserLikeDataVo;

@RestController
@RequestMapping("/user/like")
@RequiredArgsConstructor
public class UserLikeController extends BaseController {
    private final UserLikeService userLikeService;
    private final UserLikeDataService userLikeDataService;

    @AppLog("用户点赞")
    @PutMapping("/{likeDataId}")
    public LikeStatus like(@PathVariable Long likeDataId, @RequestParam Integer likeVal) {
        userLikeService.saveLike(likeDataId, likeVal);
        return userLikeDataService.getStatus(likeDataId);
    }

    @GetMapping("/msg")
    public IPage<UserLikeDataVo> getLikeMsgs(SearchParams params) {
        return userLikeDataService.search(params);
    }

    @GetMapping("/{likeDataId}")
    public UserLikeDataVo getLikeData(@PathVariable Long likeDataId) {
        return userLikeDataService.getLikeData(likeDataId);
    }

    @GetMapping("/list/{likeDataId}")
    public PageImpl<UserLike> getLikeUsers(@PathVariable Long likeDataId) {
        return userLikeService.getUserLikes(likeDataId);
    }
}