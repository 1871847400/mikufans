package pers.tgl.mikufans.entity;

import lombok.Data;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;

import java.io.Serializable;
import java.util.Map;

@Data
@PermFlag(name = "系统信息", group = PermGroup.MONITOR)
public class ServerInfo implements Serializable {
    /**
     * CPU信息
     */
    private CpuInfo cpu;
    /**
     * 内存信息
     */
    private MemInfo mem;
    /**
     * 系统信息
     */
    private SysInfo sys;
    /**
     * 磁盘信息
     */
    private DiskInfo disk;
    /**
     * JVM信息
     */
    private JvmInfo jvm;
    /**
     * 环境变量
     */
    private Map<String, String> env;

    public ServerInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        this.mem = new MemInfo(hal.getMemory());
        this.cpu = new CpuInfo(hal.getProcessor());
        this.sys = new SysInfo();
        this.disk = new DiskInfo(si.getOperatingSystem());
        this.jvm = new JvmInfo();
        this.env = System.getenv();
    }
}