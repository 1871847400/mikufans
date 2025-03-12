package pers.tgl.mikufans.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ColorUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.LimitConfig;
import pers.tgl.mikufans.config.upload.TransferConfig;
import pers.tgl.mikufans.consts.Code;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.domain.common.ImageResource;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.enums.FileUploadStatus;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.ImageResourceMapper;
import pers.tgl.mikufans.service.ImageResourceService;
import pers.tgl.mikufans.service.UploadTaskService;
import pers.tgl.mikufans.transfer.ResourceTransfer;
import pers.tgl.mikufans.transfer.ResourceTransferProvider;
import pers.tgl.mikufans.util.DbUtils;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.WebpUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Date;

/**
 * @author TGL
 * @description 针对表【static_resource(静态资源表)】的数据库操作Service实现
 * @createDate 2022-12-31 10:17:31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageResourceServiceImpl extends BaseServiceImpl<ImageResource, ImageResourceMapper> implements ImageResourceService {
    private final AppConfig appConfig;
    private final UploadTaskService uploadTaskService;
    private final ResourceTransferProvider resourceTransferProvider;

    private String getBasePath() {
        String date = DateUtil.formatDate(new Date()).substring(2);
        String base = appConfig.getUpload().get(FileMediaType.IMAGE).getPath();
        return base + File.separator + date;
    }

    private Long downloadFile(InputStream is, String mediaType) {
        if (mediaType == null || !mediaType.startsWith("image/")) {
            throw new CustomException("不支持的图片类型");
        }
        byte[] bytes = IoUtil.readBytes(is, true);
        String md5 = DigestUtils.md5DigestAsHex(bytes);
        //检查md5是否已存在
        ImageResource res = getOneBy(ImageResource::getMd5, md5);
        if (res != null) {
            return res.getId();
        }
        //检查用户是否达到上传限制
        LimitConfig limit = appConfig.getUpload().get(FileMediaType.IMAGE).getLimit();
        DbUtils.checkLimit(ImageResource.class, limit);
        String basePath = getBasePath();
        FileUtil.mkdir(basePath);
        File outFile = new File(basePath, md5 + ".webp");
        if ("image/webp".equals(mediaType)) {
            FileUtil.writeBytes(bytes, outFile);
        } else {
            ByteArrayInputStream bais = null;
            try {
                bais = new ByteArrayInputStream(bytes);
                WebpUtils.compress(bais, outFile);
            } catch (Exception e) {
                log.error("压缩图片失败,转为直接存储！", e);
                outFile = new File(basePath, md5 + ".jpg");
                FileUtil.writeBytes(bytes, outFile);
            } finally {
                IoUtil.close(bais);
            }
        }
        //保存数据库
        ImageResource resource = new ImageResource();
        try {
            resource.setLocalPath(outFile.getAbsolutePath());
            resource.setMd5(md5);
            resource.setFileSize(bytes.length);
            resource.setMediaType(mediaType);
            resource.setMainColor(getMainColor(outFile));
            resource.setResolution(getResolution(outFile));
            resource.setTransferMode(TransferMode.NONE);
            resource.setTransferPath("");
            save(resource);
        } catch (Exception e) {
            FileUtil.del(outFile);
            throw e;
        }
        getService(getClass()).transfer(outFile, resource.getId());
        return resource.getId();
    }

    @Override
    public Long uploadByTaskId(String taskId) {
        UploadTask uploadTask = uploadTaskService.getById(taskId);
        if (uploadTask == null || uploadTask.getUploadStatus() != FileUploadStatus.SUCCESS
                || !FileUtil.exist(uploadTask.getOutput())) {
            throw new CustomException(Code.RESOURCE_NOT_FOUND);
        }
        return downloadFile(FileUtil.getInputStream(uploadTask.getOutput()), uploadTask.getMediaType());
    }

    @Override
    public Long upload(MultipartFile file) {
        try {
            return downloadFile(file.getInputStream(), file.getContentType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long uploadByURL(String url) {
        if (StrUtil.isBlank(url)) {
            throw new CustomException("url参数为空");
        }
        InputStream is = null;
        try {
            is = new URL(url).openConnection().getInputStream();
            Tika tika = new Tika();
            Long resId = downloadFile(is, tika.detect(is));
            updateById(resId, ImageResource::getTransferPath, url);
            return resId;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    public void transfer(File file, Long resId) {
        TransferConfig transfer = appConfig.getUpload().get(FileMediaType.IMAGE).getTransfer();
        ResourceTransfer resourceTransfer = resourceTransferProvider.getResourceTransfer(transfer.getMode());
        if (resourceTransfer != null) {
            String date = DateUtil.formatDate(new Date()).substring(2);
            String filepath = transfer.getPath() + "/" + date + "/" + resId + "/" + file.getName();
            try {
                resourceTransfer.upload(file, filepath);
            } catch (IOException e) {
                log.error("转存图片失败", e);
                return;
            }
            ImageResource imageResource = new ImageResource();
            imageResource.setId(resId);
            imageResource.setTransferMode(transfer.getMode());
            imageResource.setTransferPath(filepath);
            updateById(imageResource);
            if (transfer.isDeleteOnSuccess()) {
                FileUtil.del(file);
            }
        }
    }

    private String getMainColor(File file) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (IOException e) {
            throw new CustomException("不支持的图片格式");
        }
        if (bufferedImage != null) {
            return ColorUtil.getMainColor(bufferedImage);
        }
        return "";
    }

    private String getResolution(File file) {
        try {
            BufferedImage bufferedImage = ImgUtil.read(file);
            return bufferedImage.getWidth() + "x" + bufferedImage.getHeight();
        } catch (Exception e) {
            throw new CustomException("不支持的图片格式");
        }
    }

    @Override
    public void downloadImage(Long id, HttpServletResponse response) throws IOException {
        ImageResource res = getById(id);
        if (res == null) {
            response.sendError(HttpStatus.HTTP_NOT_FOUND);
            return;
        }
        //优先使用转存地址
        if (StrUtil.isNotBlank(res.getTransferPath())) {
            response.sendRedirect(res.getTransferPath());
            return;
        }
        File file = new File(res.getLocalPath());
        if (FileUtil.isEmpty(file)) {
            response.sendError(HttpStatus.HTTP_NOT_FOUND);
            return;
        }
        MyUtils.writeFile(response, file, Duration.ofDays(7));
    }
}