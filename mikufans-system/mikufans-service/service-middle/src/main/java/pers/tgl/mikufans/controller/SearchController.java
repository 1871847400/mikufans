package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pers.tgl.mikufans.domain.enums.VideoType;
import pers.tgl.mikufans.params.VideoParams;
import pers.tgl.mikufans.params.UserSearchParams;
import pers.tgl.mikufans.service.UserService;
import pers.tgl.mikufans.service.VideoSearchService;
import pers.tgl.mikufans.vo.SearchResultCombine;

@RequiredArgsConstructor
@RestController
public class SearchController extends BaseController {
    private final VideoSearchService videoSearchService;
    private final UserService userService;

    @GetMapping("/search")
    public SearchResultCombine search(@RequestParam(required = false) String kw) {
        VideoParams video = new VideoParams();
        video.setType(VideoType.VIDEO);
        video.setKeyword(kw);
        video.setPageSize(0);

        VideoParams anime = new VideoParams();
        anime.setType(VideoType.ANIME);
        anime.setKeyword(kw);
        anime.setPageSize(2);

        VideoParams movie = new VideoParams();
        movie.setType(VideoType.MOVIE);
        movie.setKeyword(kw);
        movie.setPageSize(2);

        UserSearchParams user = new UserSearchParams();
        user.setNickname(kw);
        user.setPageSize(2);

        SearchResultCombine result = new SearchResultCombine();
        result.setVideo(videoSearchService.search(video));
        result.setAnime(videoSearchService.search(anime));
        result.setMovie(videoSearchService.search(movie));
        result.setUser(userService.search(user));
        return result;
    }
}