package pers.tgl.mikufans.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.tgl.mikufans.domain.system.SysAudit;
import pers.tgl.mikufans.vo.VideoAudit;

public interface VideoAuditService extends BaseService<SysAudit> {
    /**
     * 搜索所有需要审核的视频
     */
    IPage<VideoAudit> search();
    /**
     * 获取指定视频,及其所有需要审核的分集
     */
    VideoAudit getVideoAudit(Long videoId);
}