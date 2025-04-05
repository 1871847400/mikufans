package pers.tgl.mikufans.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.system.SystemUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.model.MediaSegment;
import io.lindstrom.m3u8.parser.MediaPlaylistParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.lang.Nullable;
import pers.tgl.mikufans.domain.common.CmdLog;
import pers.tgl.mikufans.domain.enums.VideoQuality;
import pers.tgl.mikufans.model.MediaBaseInfo;
import pers.tgl.mikufans.model.MediaInfo;

import javax.crypto.KeyGenerator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Process 需要在执行后 手动销毁
 * ProcessBuilder.redirectErrorStream 合并标准错误和标准输出流
 */
@Slf4j
public class FFmpegUtils {
    // 跨平台换行符
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 生成随机16个字节的AESKEY
     */
    private static byte[] genAesKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            log.error("不支持AES算法", e);
            return KeyUtil.generateKey("AES", 128).getEncoded();
        }
    }

    /**
     * 在指定的目录下生成key_info, key文件，返回key_info文件，用于加密流媒体文件
     */
    private static Path genKeyInfo(String folder) throws IOException {
        // AES 密钥
        byte[] aesKey = genAesKey();
        // AES 向量
        String iv = Hex.encodeHexString(genAesKey());
        // key 文件写入
        Path keyFile = Paths.get(folder, "key");
        Files.write(keyFile, aesKey, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        // key_info 文件写入
        StringBuilder sb = new StringBuilder();
        // m3u8加载key文件网络路径
        sb.append("key").append(LINE_SEPARATOR);
        // FFmpeg加载key文件的路径
        sb.append("key").append(LINE_SEPARATOR);
        // ASE 向量
        sb.append(iv);
        Path keyInfo = Paths.get(folder, "key_info");
        Files.write(keyInfo, sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return keyInfo;
    }

    /**
     * 将视频文件转换成m3u8,生成文件在目标文件同目录
     * 返回.m3u8文件
     * ffmpeg -i xxx -hls_segment_type mpegts -hls_playlist_type vod -hls_time 10 -c:v libx264 -c:a aac -sn -vf format=yuv420p -crf 18
     */
    public static HlsConvertResult convertToHls(File videoFile, File outFolder, boolean encrypt, @Nullable Consumer<Float> onProgress) throws Exception {
        if (!FileUtil.exist(videoFile)) {
            throw new FileNotFoundException("视频文件不存在");
        }
        //如果没有音频必须禁掉,否则报错
        boolean hasAudio;
        //码率,byte
        int videoBitRate;
        int audioBitRate;
        //视频一共多少帧,会取音频和视频中最长的
        int frameLength;
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile)) {
            grabber.start();
            if (!grabber.hasVideo()) {
                throw new IOException("不存在视频流");
            }
            hasAudio = grabber.hasAudio();
            videoBitRate = grabber.getVideoBitrate();
            audioBitRate = grabber.getAudioBitrate();
            frameLength = grabber.getLengthInFrames();
        }
        if (!outFolder.exists()) {
            FileUtil.mkdir(outFolder);
        }
        //画质文件夹路径,%v代表下标从0开始,如果指定了name:则%v代表指定的名称
        List<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-re"); //按照码率读取文件
        cmd.add("-i"); //指定视频文件
        cmd.add(videoFile.getName());
        cmd.add("-v"); //日志等级
        cmd.add("info");
        cmd.add("-hls_segment_type"); //切片类型
        cmd.add("mpegts"); //使用ts格式
        cmd.add("-hls_playlist_type"); //点播模式,会强制hls_list_size=0
        cmd.add("vod");
        cmd.add("-hls_segment_filename"); //分片路径
        cmd.add("%v/%d.ts"); //%v代表画质文件
        cmd.add("-hls_time"); //切片时间(非精准)
        cmd.add("3");
        cmd.add("-c:v"); //指定视频编码
        cmd.add("libx264");
        cmd.add("-c:a"); //指定音频编码
        cmd.add("aac");
        cmd.add("-sn"); //禁用字幕,否则字幕文件也会被切片
        cmd.add("-vf"); //video_format
        cmd.add("format=yuv420p"); // 如果是yuv420p10可能不支持手机
        StringBuilder streams = new StringBuilder();
        HlsConvertResult result = new HlsConvertResult();
        int index = 0;
        for (VideoQuality quality : VideoQuality.values()) {
            //如果指定画质的码率高于原始视频的码率,则放弃该画质的分片
            if (quality != VideoQuality.SD && quality.getVideoBitrate() > videoBitRate) {
                continue;
            }
            cmd.add("-b:v:" + index);
            //防止转换的码率高于实际码率,否则导致播放卡顿
            cmd.add(Math.min(quality.getVideoBitrate(), videoBitRate)+"");
            cmd.add("-b:a:" + index);
            cmd.add(Math.min(quality.getAudioBitrate(), audioBitRate)+"");
            cmd.add("-map");
            cmd.add("0:v");
            cmd.add("-map");
            cmd.add("0:a?"); // 问号可以防止音频不存在报错
            streams.append(" v:").append(index);
            if (hasAudio) {
                streams.append(",a:").append(index);
            }
            streams.append(",name:").append(quality.key()); //确定生成的文件夹(装分片)名称
            index++;
            result.addQuality(quality);
        }
        if (encrypt) {
            Path keyInfoPath = genKeyInfo(outFolder.getAbsolutePath());
            cmd.add("-hls_key_info_file");
            cmd.add(keyInfoPath.toString());
        }
        //则限制使用线程数量
        cmd.add("-threads");
        cmd.add(Math.max(OshiUtil.getCpuInfo().getCpuNum() / 2, 1) + "");
        //如果内存不足则调整编码速度和压缩率,减少内存占用
        if (OshiUtil.getMemory().getAvailable() < FileSizeUtil.G_SIZE) {
            cmd.add("-preset");
            cmd.add("fast");
        }
        cmd.add("-master_pl_name"); //画质路由文件
        cmd.add("master.m3u8");
        cmd.add("-var_stream_map"); //将视频同时切割成多个版本
        cmd.add("\"" + streams.toString().trim() + "\"");
        cmd.add("-f");
        cmd.add("hls");
        cmd.add("%v/playlist.m3u8");
        String cmdStr = String.join(" ", cmd);
        File outLogFile = new File(outFolder, "out.log");
        // linux 下必须使用bash -c, 否则参数使用双引号包裹空格会出错 如: -arg "a1,a2 a1,a2"
        if (SystemUtil.getOsInfo().isLinux()) {
            cmd = Arrays.asList("bash", "-c", cmdStr);
        }
        Process process = new ProcessBuilder(cmd)
                .directory(outFolder) //设置工作目录
                .redirectErrorStream(true) //合并标准输出和标准错误
//                .redirectOutput(outLogFile)
                .start();
        //读取进程的输出日志来确定当前进度
        List<String> lines = new LinkedList<>();
        try (InputStream is = process.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader bufferedReader = new BufferedReader(isr)) {
            Pattern pattern = Pattern.compile("frame=\\s*(\\d+)");
            String line;
            //这里会阻塞,如果从文件读取则不会阻塞
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
                Matcher matcher = pattern.matcher(line);
                if (!matcher.find()) {
                    continue;
                }
                int currentFrame = Integer.parseInt(matcher.group(1));
                float progress = MathUtils.clamp(currentFrame / (float) frameLength, 0, 1);
                if (onProgress != null) {
                    onProgress.accept(progress);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        //写入日志文件
        FileUtil.writeUtf8Lines(lines, outLogFile);
        //等待进程结束
        int waitFor = process.waitFor();
        process.destroy();
        CmdLog cmdLog = new CmdLog();
        cmdLog.setCmd(cmdStr);
        cmdLog.setOutput(CollUtil.join(lines, LINE_SEPARATOR));
        cmdLog.setExitCode(waitFor);
        Db.save(cmdLog);
        return result;
    }

    public static Duration parseMediaFileDuration(File videoFile, Duration def) {
        try {
            return parseMediaFile(videoFile).getDuration();
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取视频文件的元信息
     * @param file 视频文件 或 .m3u8(所有分片文件也必须在同一个目录下)
     */
    public static MediaBaseInfo parseMediaFile(File file) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(file.getAbsolutePath());
        String outInfo = exec(command, null);
        // Duration: 00:09:42.55, start: 0.000000, bitrate: 903 kb/s
        Duration duration = Duration.ofMillis(0);
        Pattern pattern = Pattern.compile("Duration: (\\d+:\\d+:\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(outInfo);
        if (matcher.find()) {
            String str = matcher.group(1);
            String[] split = str.split(":");
            duration = duration.plusHours(Integer.parseInt(split[0]));
            duration = duration.plusMinutes(Integer.parseInt(split[1]));
            duration = duration.plusSeconds(Integer.parseInt(split[2].split("\\.")[0]));
            duration = duration.plusMillis(Integer.parseInt(split[2].split("\\.")[1]) * 10L);
        }
        int fps = 0;
        pattern = Pattern.compile("(\\d+)\\sfps");
        matcher = pattern.matcher(outInfo);
        if (matcher.find()) {
            String str = matcher.group(1);
            fps = Integer.parseInt(str);
        }
        return new MediaBaseInfo(duration, fps);
    }

    /**
     * 直接解析m3u8文件，不需要块文件
     */
    public static Duration computeDuration(File m3u8) {
        MediaPlaylist mediaPlaylist = parseM3U8File(m3u8);
        double time = 0;
        if (mediaPlaylist != null) {
            for (MediaSegment mediaSegment : mediaPlaylist.mediaSegments()) {
                time += mediaSegment.duration();
            }
        }
        return Duration.ofMillis((long) (time * 1000));
    }

    /**
     * 注意无法通过此对象获取总时长
     */
    @Nullable
    public static MediaPlaylist parseM3U8File(File m3u8) {
        MediaPlaylist mediaPlaylist = null;
        if (m3u8.exists()) {
            MediaPlaylistParser parser = new MediaPlaylistParser();
            try {
                mediaPlaylist = parser.readPlaylist(Files.newInputStream(m3u8.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mediaPlaylist;
    }

    public static MediaInfo parse(String filepath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("ffprobe");
        command.add("-i");
        command.add(filepath);
        command.add("-show_format");
        command.add("-show_streams");
        command.add("-print_format");
        command.add("json");
        command.add("-v");
        command.add("quiet");
        String output = exec(command, null);
        return JsonUtils.read(output, MediaInfo.class);
    }

    /**
     * 视频截图
     * @param filepath 文件绝对路径
     * @param offset   截图时间 HH:mm:ss.[SSS] 可以直接使用数字, 90 代表偏移90秒
     * @param quality 品质 1最高
     * @param outPath  生成的图片绝对路径 .png .jpg ...
     */
    public static void screenshot(String filepath, String offset, int quality, String outPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(filepath);
        command.add("-loglevel");
        command.add("info");
        command.add("-ss");
        command.add(offset);
        command.add("-y");
        command.add("-q:v"); //品质
        command.add(quality+"");
        command.add("-frames:v");
        command.add("1");
        command.add("-f");
        command.add("image2");
        command.add(outPath);
        exec(command, null);
    }

    /**
     * 将视频指定区间分离出来
     * @param offset 偏移时长(s)
     * @param length 获取长度(s) null代表剩下全部
     */
    public static void separate(File videoFile, File outFile, float offset, @Nullable Float length) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(videoFile.getAbsolutePath());
        command.add("-loglevel");
        command.add("info");
        if (offset > 0) {
            command.add("-ss");
            command.add(offset+"");
        }
        if (length != null && length > 0) {
            command.add("-t");
            command.add(length+"");
        }
        command.add(outFile.getAbsolutePath());
        exec(command, null);
    }

    /**
     * 合并两个视频,输出文件路径可以和合并文件相同
     * 输出格式必须有后缀名
     */
    public static void merge(File file1, File file2, File outFile) throws Exception {
        //防止生成的文件和合并要使用的文件相同路径
        String tempName = IdUtil.fastSimpleUUID() + "." + FileUtil.getSuffix(outFile);
        File tempFile = new File(outFile.getParentFile(), tempName);
        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(file1.getAbsolutePath());
        command.add("-i");
        command.add(file2.getAbsolutePath());
        command.add("-filter_complex");
        command.add("concat");
        command.add("-loglevel");
        command.add("info");
        command.add(tempFile.getAbsolutePath());
        exec(command, null);
        FileUtil.rename(tempFile, outFile.getName(), true);
    }

    /**
     * 嵌入字幕,会以Stream的形式加入
     */
    public static void addSubtitleStream(File videoFile, File subtitleFile, File outFile) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-i");
        cmd.add(videoFile.getAbsolutePath());
        cmd.add("-vf");
        cmd.add("subtitles=" + subtitleFile.getAbsolutePath());
        cmd.add(outFile.getAbsolutePath());
        exec(cmd, null);
    }

    /**
     * 嵌入图片
     * outFile 必须是视频格式的后缀
     */
    public static void overlayImage(File videoFile, File imgFile, File outFile, float x, float y) throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add("ffmpeg");
        cmd.add("-i");
        cmd.add(videoFile.getAbsolutePath());
        cmd.add("-i");
        cmd.add(imgFile.getAbsolutePath());
        cmd.add("-filter_complex");
        cmd.add("overlay=" + x + ":" + y);
        cmd.add(outFile.getAbsolutePath());
        exec(cmd, null);
    }

    public static String exec(String cmd, File dir) throws Exception {
        return exec(ListUtil.of(cmd.split(" ")), dir);
    }

    /**
     * @param cmd 命令必须拆开成数组
     * @param dir 工作目录 默认为当前程序运行的目录
     * @return 日志
     * @throws Exception
     */
    public static String exec(List<String> cmd, @Nullable File dir) throws Exception {
        Process process = null;
        InputStream is = null;
        String output = "";
        String errorMsg = "";
        int waitFor = 1;
        try {
            process = new ProcessBuilder(cmd)
                    .directory(dir)
                    .redirectErrorStream(true) //合并标准输出和标准错误
                    .start();
            log.info(CollUtil.join(cmd, " "));
            is = process.getInputStream();
            output = IoUtil.read(is, StandardCharsets.UTF_8);
            waitFor = process.waitFor();
        } catch (Exception e) {
            log.error("执行命令失败", e);
            errorMsg = e.getMessage();
            throw e;
        } finally {
            IoUtil.close(is);
            if (process != null) {
                process.destroy();
            }
            CmdLog cmdLog = new CmdLog();
            cmdLog.setCmd(CollUtil.join(cmd, " "));
            cmdLog.setOutput(output);
            cmdLog.setErrorMsg(errorMsg);
            cmdLog.setExitCode(waitFor);
            //方便调试
            if (TableInfoHelper.getTableInfo(CmdLog.class) != null) {
                Db.save(cmdLog);
            }
        }
        return output;
    }
}