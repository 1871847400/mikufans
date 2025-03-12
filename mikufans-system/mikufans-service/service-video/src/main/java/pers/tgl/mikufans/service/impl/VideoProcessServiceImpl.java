package pers.tgl.mikufans.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.system.oshi.OshiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pers.tgl.mikufans.consts.VideoProcessStage;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.event.VideoPartProcessEvent;
import pers.tgl.mikufans.mapper.VideoProcessMapper;
import pers.tgl.mikufans.service.VideoProcessService;
import pers.tgl.mikufans.util.MathUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoProcessServiceImpl extends BaseServiceImpl<VideoProcess, VideoProcessMapper> implements VideoProcessService {
    /**
     * 最大任务并行数量
     */
    private static final int MAX_THREAD_COUNT = Math.max(OshiUtil.getCpuInfo().getCpuNum() / 2, 1);
    /**
     * 线程池
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
    /**
     * 当前正在进行中的任务
     */
    private final Set<Long> processIds = CollUtil.newHashSet();
    /**
     * 任务id : 失败次数
     */
    private final Map<Long, Integer> errors = new HashMap<>();
    /**
     * 任务id : 进度(0-100)
     */
    private final Map<Long, Integer> progresses = new HashMap<>();

    @Scheduled(initialDelay = 5, fixedDelay = 12, timeUnit = TimeUnit.SECONDS)
    public void start() {
        if (processIds.size() >= MAX_THREAD_COUNT) {
            return;
        }
        List<VideoProcess> list = wrapper()
                .eq(VideoProcess::getResult, 0)
                .notIn(CollUtil.isNotEmpty(processIds), VideoProcess::getId, processIds)
                .orderByAsc(VideoProcess::getCreateTime)
                .last("limit " + MAX_THREAD_COUNT)
                .list();
        for (VideoProcess process : list) {
            if (FileUtil.isEmpty(process.getVideoFile())) {
                log.error("视频原始文件丢失: {}", process.getId());
                continue;
            }
            VideoProcessStage stage = VideoProcessStage.fromCode(process.getStage());
            if (stage == null) {
                log.error("无效阶段: {}", process.getId());
                continue;
            }
            List<VideoProcessStage> stages = Arrays.stream(VideoProcessStage.values())
                    .filter(s -> s.ordinal() >= stage.ordinal())
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(stages)) {
                log.error("异常阶段: {}", process.getId());
                continue;
            }
            processIds.add(process.getId());
            executorService.submit(()->startProcess(process, stages));
            log.info("提交新的视频处理任务,id={},stage={}", process.getId(), stage);
        }
    }

    private void startProcess(VideoProcess process, List<VideoProcessStage> stages) {
        boolean success = false;
        try {
            for (VideoProcessStage stage : stages) {
                final int progress = computeProgress(stage.getBeforePowers());
                //设置当前阶段和进度
                process.setStage(stage.getCode());
                process.setProgress(progress);
                progresses.put(process.getId(), progress);
                //同时更新数据库
                updateById(process.getId(), VideoProcess::getStage, stage.getCode());
                updateById(process.getId(), VideoProcess::getProgress, progress);
                //执行对应阶段的方法
                SpringUtil.getBean(stage.getHandlerClass()).handle(process, value->{
                    int newProgress = computeProgress(stage.getBeforePowers() + value * stage.getPower());
                    progresses.put(process.getId(), newProgress);
                    process.setProgress(newProgress);
                });
            }
            //如果全部阶段执行完成
            updateById(process.getId(), VideoProcess::getProgress, 100);
            updateById(process.getId(), VideoProcess::getResult, 1);
            success = true;
        } catch (Exception e) {
            int errorCount = errors.getOrDefault(process.getId(), 0);
            //最多重试3次
            if (errorCount >= 2) {
                updateById(process.getId(), VideoProcess::getResult, 2);
                errors.remove(process.getId());
            } else {
                errors.put(process.getId(), errorCount + 1);
            }
            updateById(process.getId(), VideoProcess::getErrorMsg, e.getMessage());
            log.error("视频处理异常！ id:{} stage:{} count:{}", process.getId(), process.getStage(), errorCount, e);
        } finally {
            processIds.remove(process.getId());
            progresses.remove(process.getId());
        }
        if (success) {
            errors.remove(process.getId());
            publishEvent(new VideoPartProcessEvent(this, process));
        }
    }

    private int computeProgress(Number power) {
        int value = Math.round(power.floatValue() / VideoProcessStage.getTotalPower() * 100F);
        return MathUtils.clamp(value, 0, 100);
    }

    @Override
    public Map<String, String> getProcessStatus() {
        Map<String, String> map = new LinkedHashMap<>(2);
        String label;
        String type;
        if (processIds.isEmpty()) {
            label = "空闲";
            type = "success";
        } else if (processIds.size() <= MAX_THREAD_COUNT / 3) {
            label = "良好";
            type = "primary";
        } else if (processIds.size() <= MAX_THREAD_COUNT / 2) {
            label = "繁忙";
            type = "warning";
        } else {
            label = "极其繁忙";
            type = "error";
        }
        map.put("label", label);
        map.put("type", type);
        return map;
    }

    @Override
    public int getProgress(Long processId) {
        if (progresses.containsKey(processId)) {
            return progresses.get(processId);
        }
        return Optional.ofNullable(getColumnValue(processId, VideoProcess::getProgress))
                .orElse(0);
    }
}