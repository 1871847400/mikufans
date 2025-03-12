package pers.tgl.mikufans.schedule;

import pers.tgl.mikufans.domain.video.VideoProcess;

import java.util.function.Consumer;

public interface VideoProcessHandler {
    void handle(VideoProcess process, Consumer<Float> progress) throws Exception;
}