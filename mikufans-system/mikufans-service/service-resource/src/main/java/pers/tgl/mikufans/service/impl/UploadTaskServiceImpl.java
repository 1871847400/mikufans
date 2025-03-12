package pers.tgl.mikufans.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.config.AppConfig;
import pers.tgl.mikufans.config.upload.UploadConfig;
import pers.tgl.mikufans.consts.FileMediaType;
import pers.tgl.mikufans.controller.BaseController;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.enums.FileUploadStatus;
import pers.tgl.mikufans.dto.UploadTaskDto;
import pers.tgl.mikufans.exception.CustomException;
import pers.tgl.mikufans.mapper.UploadTaskMapper;
import pers.tgl.mikufans.service.UploadTaskService;
import pers.tgl.mikufans.util.MyUtils;
import pers.tgl.mikufans.util.SecurityUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadTaskServiceImpl extends BaseServiceImpl<UploadTask, UploadTaskMapper> implements UploadTaskService {
    /**
     * 上传的临时文件过期时间
     * 每次上传分片后应该刷新时间
     * 到期前应该支持断点续传
     */
    private static final Duration EXPIRE_TIMEOUT = Duration.ofDays(3);
    /**
     * 上传完成后的过期时间
     * 到期前再次上传同一个文件,不应该花费时间
     */
    private static final Duration DONE_TIMEOUT = Duration.ofDays(7);

    private final AppConfig appConfig;

    @Override
    public Page<UploadTask> findAllTasks(List<FileUploadStatus> statusList) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        return lambdaQuery()
                .eq(UploadTask::getUserId, contextUserId)
                .in(CollUtil.isNotEmpty(statusList), UploadTask::getUploadStatus, statusList)
                .page(BaseController.createPage());
    }

    @Override
    public UploadTask createTask(UploadTaskDto dto) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        List<UploadTask> list = lambdaQuery()
                .eq(UploadTask::getMd5, dto.getMd5())
                .eq(UploadTask::getChunkSize, dto.getChunkSize())
                .eq(UploadTask::getRemoved, 0)
                .gt(UploadTask::getExpireAt, new Date())
                .in(UploadTask::getUploadStatus,
                        FileUploadStatus.SUCCESS,
                        FileUploadStatus.PAUSED,
                        FileUploadStatus.UPLOADING)
                .eq(UploadTask::getUserId, contextUserId)
                .orderByDesc(UploadTask::getUpdateTime)
                .list();
        Date expireAt = DateUtil.offsetMinute(new Date(), (int) EXPIRE_TIMEOUT.toMinutes());
        //如果之前的上传任务还有效则返回之前的
        if (CollUtil.isNotEmpty(list)) {
            updateById(list.get(0).getId(), UploadTask::getExpireAt, expireAt);
            return list.get(0);
        }
        FileMediaType fileMediaType = FileMediaType.getType(dto.getMediaType());
        UploadConfig uploadConfig = appConfig.getUpload().get(fileMediaType);
        if (uploadConfig == null || StrUtil.isBlank(FileUtil.getSuffix(dto.getFileName()))) {
            throw new CustomException("不支持的文件类型");
        }
        //限制单文件的大小
        DataSize fileSize = uploadConfig.getFilesize();
        if (dto.getFileSize() > fileSize.toBytes()) {
            if (!SecurityUtils.isAdminUser()) {
                throw new CustomException("您最大只能上传: " + fileSize.toMegabytes()+"MB");
            }
        }
        UploadTask uploadTask = BeanUtil.toBean(dto, UploadTask.class);
        uploadTask.setMediaType(dto.getMediaType().toString());
        uploadTask.setChunkCount(dto.getChunkCount());
        uploadTask.setChunkCode(0);
        uploadTask.setUploadSize(0);
        uploadTask.setRemoved(0);
        uploadTask.setUploadStatus(FileUploadStatus.PAUSED);
        uploadTask.setFilePath(uploadConfig.getUploadingPath() +  File.separator + IdUtil.fastSimpleUUID());
        uploadTask.setExpireAt(expireAt);
        save(uploadTask);
        return uploadTask;
    }

    @Override
    public void uploadChunk(Long taskId, MultipartFile file, int chunkCode) {
        UploadTask uploadTask = getUploadTask(taskId);
        if (chunkCode < 1 || chunkCode > uploadTask.getChunkCount()) {
            throw new CustomException("分片下标超出范围");
        }
        File folder = FileUtil.mkdir(uploadTask.getFilePath());
        File chunkFile = new File(folder, "chunk-" + chunkCode + ".tmp");
        try {
            file.transferTo(chunkFile);
        } catch (IOException e) {
            log.error("转存文件失败", e);
            throw new CustomException("上传文件失败,请重新上传！");
        }
        int curSize = (int) (uploadTask.getUploadSize() + chunkFile.length());
        if (curSize > uploadTask.getFileSize()) {
            updateById(uploadTask.getId(), UploadTask::getUploadStatus, FileUploadStatus.FAILURE);
            throw new CustomException("超出预期上传文件大小,请重新上传！");
        }
        lambdaUpdate().eq(UploadTask::getId, taskId)
                .set(UploadTask::getUploadSize, curSize)
                .set(UploadTask::getUploadStatus, FileUploadStatus.UPLOADING)
                .set(UploadTask::getChunkCode, chunkCode)
                .set(UploadTask::getExpireAt, DateUtil.offsetMinute(new Date(), (int) EXPIRE_TIMEOUT.toMinutes()))
                .update(new UploadTask());
    }

    @Override
    public void mergeChunks(Long taskId) {
        UploadTask uploadTask = getUploadTask(taskId);
        if (!Objects.equals(uploadTask.getUploadSize(), uploadTask.getFileSize())
                || !Objects.equals(uploadTask.getChunkCode(), uploadTask.getChunkCount())) {
            throw new CustomException("上传分片不完整");
        }
        File folder = new File(uploadTask.getFilePath());
        //检查分片是否存在
        if (FileUtil.isEmpty(folder)) {
            updateById(uploadTask.getId(), UploadTask::getUploadStatus, FileUploadStatus.FAILURE);
            throw new CustomException("文件已丢失,请重新上传！");
        }
        //找到所有文件的分片,按照code进行排序
        List<File> chunks = FileUtil.loopFiles(folder, 1, f -> f.getName().endsWith(".tmp"));
        chunks.sort(Comparator.comparingInt(f -> MyUtils.extractInteger(f.getName(), -1)));
        //合并后的文件
        File mergeFile = uploadTask.getOutput();
        //先删除一次防止已存在
        FileUtil.del(mergeFile);
        //将排序后的分片进行合并
        for (File chunk : chunks) {
            FileUtil.writeBytes(FileUtil.readBytes(chunk), mergeFile, 0, (int) chunk.length(), true);
        }
        //合并后删除分片
        chunks.forEach(FileUtil::del);
        //校验文件hash值
        String md5 = DigestUtil.md5Hex(mergeFile);
        if (!Objects.equals(md5, uploadTask.getMd5())) {
            updateById(uploadTask.getId(), UploadTask::getUploadStatus, FileUploadStatus.FAILURE);
            throw new CustomException("哈希值校验失败,请重新上传！");
        }
        lambdaUpdate().eq(UploadTask::getId, taskId)
                .set(UploadTask::getUploadStatus, FileUploadStatus.SUCCESS)
                .set(UploadTask::getExpireAt, DateUtil.offsetMinute(new Date(), (int) DONE_TIMEOUT.toMinutes()))
                .update(new UploadTask());
    }

    private UploadTask getUploadTask(Long taskId) {
        Long contextUserId = SecurityUtils.getContextUserId(true);
        UploadTask uploadTask = getById(taskId);
        if (uploadTask == null || !Objects.equals(contextUserId, uploadTask.getUserId())
                || uploadTask.isExpired() || uploadTask.getUploadStatus().isDone()) {
            throw new CustomException("任务已失效");
        }
        return uploadTask;
    }

    /**
     * 每隔一段时间清理过期的上传任务产生的文件
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void cycleTask() {
        //所有到期但还没有删除源文件的任务
        List<UploadTask> list = lambdaQuery()
                .eq(UploadTask::getRemoved, 0)
                .lt(UploadTask::getExpireAt, new Date())
                .list();
        for (UploadTask uploadTask : list) {
            if (!uploadTask.getUploadStatus().isDone()) {
                //有效任务到期,设置为失败状态
                updateById(uploadTask.getId(), UploadTask::getUploadStatus, FileUploadStatus.FAILURE);
            }
            if (FileUtil.exist(uploadTask.getFilePath())) {
                FileUtil.loopFiles(uploadTask.getFilePath(), f->f.getName().endsWith(".tmp"))
                        .forEach(FileUtil::del);
                //删除合并后的最终文件
                FileUtil.del(uploadTask.getOutput());
                //删除生成的所有文件后,如果文件夹为空才删除文件夹
                if (FileUtil.isDirEmpty(new File(uploadTask.getFilePath()))) {
                    FileUtil.del(uploadTask.getFilePath());
                }
            }
            //设置已删除文件夹的标记,防止重复删除
            updateById(uploadTask.getId(), UploadTask::getRemoved, 1);
        }
    }
}