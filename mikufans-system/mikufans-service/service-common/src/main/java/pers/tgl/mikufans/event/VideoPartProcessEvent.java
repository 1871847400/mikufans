package pers.tgl.mikufans.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pers.tgl.mikufans.domain.video.VideoProcess;

/**
 * 视频分集预处理完成,接下来应该交给审核
 */
@Getter
public class VideoPartProcessEvent extends ApplicationEvent {
    private final VideoProcess videoProcess;

    public VideoPartProcessEvent(Object source, VideoProcess videoProcess) {
        super(source);
        this.videoProcess = videoProcess;
    }
}
