package pers.tgl.mikufans.schedule.handler;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.domain.video.VideoProcess;
import pers.tgl.mikufans.schedule.VideoProcessHandler;
import pers.tgl.mikufans.util.FFmpegUtils;
import pers.tgl.mikufans.util.FileComparator;
import pers.tgl.mikufans.util.MyUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 生成视频播放缩略图 (VTT索引 和 精灵图)
 * 在视频的进度条上能够看到
 */
@Component
@Slf4j
public class ThumbnailHandler implements VideoProcessHandler {
    //截图间隔 秒
    private static final int interval = 3;
    //单张截图大小
    private static final int width = 192;
    private static final int height = 108;

    @Override
    public void handle(VideoProcess process, Consumer<Float> onProgress) throws Exception {
        File videoFile = process.getVideoFile();
        Duration duration = FFmpegUtils.parseMediaFileDuration(videoFile, null);
        if (duration == null) {
            throw new IOException("无法计算视频时长");
        }
        File screenshot = new File(videoFile.getParentFile(), "screenshot");
        FileUtil.mkdir(screenshot);
        String cmd = String.format("ffmpeg -i %s -vf fps=1/%s -y -q:v 100 %s.jpg",
                videoFile.getName(), interval, screenshot.getName() + File.separator + "%d");
        FFmpegUtils.exec(cmd, screenshot.getParentFile());
        File[] files = screenshot.listFiles(f -> f.getName().endsWith(".jpg"));
        if (files == null || files.length == 0) {
            log.error("无法生成视频预览图 id={}", process.getId());
            return;
        }
        //总共截图的数量
        int total = files.length;
        //确定列数
        int cols = (int) Math.sqrt(total);
        //精灵图大小
        int spriteWidth = cols * width;
        int spriteHeight = (int) Math.ceil((double) total / cols) * height;
        //创建精灵图,默认背景为黑色
        BufferedImage sprite = new BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_RGB);
        int col = 0;
        int row = 0;
        String vtt = "WEBVTT";
        String vttPath = videoFile.getParent() + File.separator + "thumbnails.vtt";
        String spriteName = "thumbnails.jpg";
        String spritePath = videoFile.getParent() + File.separator + spriteName;

        List<File> listFiles = Arrays.stream(files)
                .sorted(new FileComparator<>(File::getName))
                .collect(Collectors.toList());
        int i = 0;
        for (File file : listFiles) {
            BufferedImage img = readImage(file);
            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    sprite.setRGB(x + col * width, y + row * height, img.getRGB(x, y));
                }
            }
            String record = "\n\n";
            record += MyUtils.formatSeconds(i);
            record += " --> ";
            record += MyUtils.formatSeconds(Math.min(i + interval, duration.getSeconds()));
            record += "\n";
            record += spriteName + "#xywh=";
            record += col * width + ",";
            record += row * height + ",";
            record += width + ",";
            record += height;
            vtt += record;

            col++;
            if (col >= cols) {
                col = 0;
                row++;
            }
            i += interval;
        }
        ImageIO.write(sprite, "jpg", new File(spritePath));
        FileUtil.writeUtf8String(vtt, vttPath);
    }

    private BufferedImage readImage(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        //改变图片大小
        Image scaledInstance = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        img = new BufferedImage(scaledInstance.getWidth(null),
                scaledInstance.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = img.createGraphics();
        graphics.drawImage(scaledInstance, null, null);
        graphics.dispose();
        return img;
    }
}
