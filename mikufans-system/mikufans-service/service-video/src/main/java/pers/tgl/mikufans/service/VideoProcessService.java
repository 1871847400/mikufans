package pers.tgl.mikufans.service;

import pers.tgl.mikufans.domain.video.VideoProcess;

import java.util.Map;

public interface VideoProcessService extends BaseService<VideoProcess> {
    /**
     * 获取状态情况
     */
    Map<String, String> getProcessStatus();
    /**
     * 获取处理进度(0-100)
     */
    int getProgress(Long processId);
}