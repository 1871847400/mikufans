package pers.tgl.mikufans.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 必须通过cd获取res资源,中转
 */
@RestController
@RequestMapping("/resource/audio")
public class AudioResController {
//    @Resource
//    private AnimeCDService animeCDService;

//    @Resource
//    private VideoResourceService videoResourceService;

    @GetMapping("/anime/cd/{cdId}")
    public void fetchResource(@PathVariable Long cdId, HttpServletResponse response) throws IOException {
//        AnimeCD cd = animeCDService.getById(cdId);
//        if (cd == null) {
//            throw new CustomException("不存在的CD");
//        }
//        File index = videoResourceService.getStorage(cd.getResId(), "index.m3u8");
//        if (!index.exists()) {
//            response.sendError(HttpStatus.NOT_FOUND.value());
//            return;
//        }
//        MyUtils.writeFile(response, index, Duration.ofMinutes(30));
    }

    @GetMapping("/segment/download/anime/cd/{cdId}")
    public void fetchSegment(@PathVariable Long cdId, @RequestParam String uri, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        AnimeCD cd = animeCDService.getById(cdId);
//        if (cd == null) {
//            throw new CustomException("不存在的CD");
//        }
//        SegmentDTO segmentDTO = new SegmentDTO();
//        segmentDTO.setUri(uri);
//        // todo
//        videoResourceService.fetchSegment(cd.getResId(), segmentDTO, request, response);
    }
}