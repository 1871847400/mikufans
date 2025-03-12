package pers.tgl.mikufans.entity;

import lombok.Data;
import pers.tgl.mikufans.util.FileSizeUtil;
import pers.tgl.mikufans.util.MyUtils;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Date;

@Data
public class JvmInfo implements Serializable {
    /**
     * java安装目录
     */
    private String home;
    /**
     * java版本
     */
    private String version;
    /**
     * java发行商
     */
    private String vendor;
    /**
     * 当前JVM占用的内存总数
     */
    private String total;
    /**
     * JVM最大可用内存总数
     */
    private String max;
    /**
     * JVM空闲内存
     */
    private String free;
    /**
     * JVM启动时间
     */
    private Date startTime;
    /**
     * JVM运行时长
     */
    private String runTime;
    /**
     * 运行参数
     */
    private String inputArgs;

    public JvmInfo() {
        this.home = System.getProperty("java.home");
        this.version = System.getProperty("java.version");
        this.vendor = System.getProperty("java.vendor");
        this.total = FileSizeUtil.formatFileSize(Runtime.getRuntime().totalMemory());
        this.max = FileSizeUtil.formatFileSize(Runtime.getRuntime().maxMemory());
        this.free = FileSizeUtil.formatFileSize(Runtime.getRuntime().freeMemory());
        this.startTime = new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
        this.runTime = MyUtils.formatChineseDuration(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()));
        this.inputArgs = String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments());
    }
}