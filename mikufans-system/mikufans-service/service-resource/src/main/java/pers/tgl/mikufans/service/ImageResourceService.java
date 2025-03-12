package pers.tgl.mikufans.service;

import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.domain.common.ImageResource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* @author TGL
* @description 针对表【static_resource(静态资源表)】的数据库操作Service
* @createDate 2022-12-31 10:17:31
*/
public interface ImageResourceService extends BaseService<ImageResource> {
    /**
     * 用户上传图片文件
     */
    Long upload(MultipartFile files);
    /**
     * 用户上传第三方链接作为图片
     */
    Long uploadByURL(String url);
    /**
     * 用户上传大文件
     */
    Long uploadByTaskId(String taskId);
    /**
     * 下载图片
     */
    void downloadImage(Long id, HttpServletResponse response) throws IOException;
}