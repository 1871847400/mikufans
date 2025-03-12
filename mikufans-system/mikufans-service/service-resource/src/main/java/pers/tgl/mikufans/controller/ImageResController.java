package pers.tgl.mikufans.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.domain.common.ImageResource;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.service.ImageResourceService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageResController extends BaseController {
    private final ImageResourceService imageResourceService;
    /**
     * 上传单个图片(有大小限制)
     */
    @AppLog("上传图片")
    @PostMapping("/upload")
    public Long upload(@RequestPart MultipartFile file) {
        return imageResourceService.upload(file);
    }
    /**
     * 上传第三方链接当做图片
     */
    @AppLog("上传图片")
    @PostMapping("/upload/url")
    public Long upload(@RequestBody Map<String, String> urlMap) {
        return imageResourceService.uploadByURL(urlMap.get("url"));
    }
    /**
     * 上传大文件图片
     */
    @AppLog("上传图片")
    @PostMapping("/upload/task/{taskId}")
    public Long upload(@PathVariable String taskId) {
        return imageResourceService.uploadByTaskId(taskId);
    }
    /**
     * 根据资源id下载图片
     */
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id) throws IOException {
        imageResourceService.downloadImage(id, getResponse());
    }
    /**
     * 根据资源id获取图片信息
     */
    @GetMapping("/{resId}")
    public ImageResource getById(@PathVariable Long resId) {
        ImageResource resource = imageResourceService.getById(resId);
        if (resource == null) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        return resource;
    }
    /**
     * 通过md5获取图片资源信息
     */
    @GetMapping("/hash/{hash}")
    public ImageResource getByHash(@PathVariable String hash) {
        return imageResourceService.getOneBy(ImageResource::getMd5, hash);
    }
}