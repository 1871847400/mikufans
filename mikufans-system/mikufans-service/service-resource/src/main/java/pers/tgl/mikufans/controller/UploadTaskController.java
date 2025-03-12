package pers.tgl.mikufans.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.aop.AppLog;
import pers.tgl.mikufans.aop.repeat.RepeatSubmit;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.enums.FileUploadStatus;
import pers.tgl.mikufans.dto.UploadTaskDto;
import pers.tgl.mikufans.service.UploadTaskService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadTaskController extends BaseController {
    private final UploadTaskService uploadTaskService;

    @GetMapping("/{taskId}")
    public UploadTask getUploadTask(@PathVariable Long taskId) {
        return uploadTaskService.getById(taskId);
    }

    @GetMapping("/list")
    public Page<UploadTask> getList(@RequestParam(required = false) List<FileUploadStatus> status) {
        return uploadTaskService.findAllTasks(status);
    }

    @AppLog("创建大文件上传任务")
    @RepeatSubmit(interval = 500)
    @PostMapping("/create")
    public UploadTask createUploadTask(@RequestBody @Validated UploadTaskDto dto) {
        return uploadTaskService.createTask(dto);
    }

    @AppLog("上传大文件分片")
    @PostMapping("/chunk/{taskId}/{chunkCode}")
    public void uploadChunk(@RequestPart MultipartFile file,
                            @PathVariable Integer chunkCode,
                            @PathVariable Long taskId) {
        uploadTaskService.uploadChunk(taskId, file, chunkCode);
    }

    @AppLog("合并所有大文件分片")
    @PostMapping("/merge/{taskId}")
    public void mergeChunks(@PathVariable Long taskId) {
        uploadTaskService.mergeChunks(taskId);
    }
}