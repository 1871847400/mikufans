package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.EnumUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.domain.video.VideoResource;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.service.VideoResourceService;
import pers.tgl.mikufans.transfer.ResourceTransfer;
import pers.tgl.mikufans.transfer.ResourceTransferProvider;
import pers.tgl.mikufans.util.MyUtils;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 视频转储
 * 将视频分片的结果存储到其它地方
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TransferHandler implements VideoProcessHandler {
    private final ResourceTransferProvider resourceTransferProvider;
    private final VideoResourceService videoResourceService;
    private final AppConfig appConfig;

    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        UploadConfig config = appConfig.getUpload().get(FileMediaType.VIDEO);
        TransferMode mode = config.getTransfer().getMode();
        if (mode == TransferMode.NONE) {
            return;
        }
        ResourceTransfer resourceTransfer = resourceTransferProvider.getResourceTransfer(mode);
        File storage = videoResourceService.getStorage(process.getResId(), null);
        //过滤为存在的画质文件夹
        List<File> folders = FileUtil.loopFiles(storage, 1, f->{
            return f.isDirectory() && EnumUtil.contains(VideoQuality.class, f.getName().toUpperCase());
        });
        //需要上传的总大小(并非实际)
        final long total = folders.stream().mapToLong(FileUtil::size).sum();
        //已上传的大小
        long uploaded = 0;
        //当前日期例如：24-12-31
        String date = DateUtil.formatDate(new Date()).substring(2);
        //基础路径
        String baseDir = config.getTransfer().getPath() + "/" + date + "/" + process.getVideoId() + "/" + process.getResId();
        //遍历画质文件夹
        for (File folder : folders) {
            //将当前画质的所有分片按顺序集合起来
            List<File> chunks = FileUtil.loopFiles(folder, 1, f->f.getName().endsWith(".ts"))
                    .stream()
                    .sorted(Comparator.comparingInt(a -> MyUtils.extractInteger(a.getName(), 0)))
                    .collect(Collectors.toList());
            if (chunks.isEmpty()) {
                continue;
            }
            for (File chunk : chunks) {
                String filepath = baseDir + "/" + folder.getName() + "/" + chunk.getName();
                resourceTransfer.upload(chunk, filepath);
                uploaded += chunk.length();
                onProgress.accept(uploaded / (float) total);
            }
            //上传完成后删除分片文件,只留下m3u8文件
            if (config.getTransfer().isDeleteOnSuccess()) {
                chunks.forEach(FileUtil::del);
            }
        }
        VideoResource resource = new VideoResource();
        resource.setId(process.getResId());
        resource.setTransferMode(mode);
        resource.setTransferPath(baseDir);
        Db.updateById(resource);
    }
}