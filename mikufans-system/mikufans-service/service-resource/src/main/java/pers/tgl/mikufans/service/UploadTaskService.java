package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;
import pers.tgl.mikufans.domain.common.UploadTask;
import pers.tgl.mikufans.domain.enums.FileUploadStatus;
import pers.tgl.mikufans.dto.UploadTaskDto;

import java.util.List;

public interface UploadTaskService extends BaseService<UploadTask> {
    /**
     * 获取上传任务
     */
    Page<UploadTask> findAllTasks(List<FileUploadStatus> statusList);
    /**
     * 创建任务前应该判断hash值是否已存在
     * 创建上传大文件的任务,任务绑定用户id
     * 随机生成一个uuid当做任务id,然后在临时文件夹中创建一个文件夹来存放上传的内容
     * 需要设置任务的到期时间,到期后删除临时文件夹
     * @return 返回任务id
     */
    UploadTask createTask(UploadTaskDto dto);
    /**
     * 上传大文件的分片(分片传输)
     * 每次上传完一个分片后刷新任务的过期时间
     */
    void uploadChunk(Long taskId, MultipartFile file, int chunkCode);
    /**
     * 1.将所有分片合并成大文件
     * 2.修改redis中的上传任务到期时间
     * 3.redis中存放hash对应uploadId,方便通过hash查找文件，以免二次上传
     */
    void mergeChunks(Long taskId);
}