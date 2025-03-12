package pers.tgl.mikufans.entity;

import lombok.Data;
import pers.tgl.mikufans.util.IpUtils;

import java.io.Serializable;

@Data
public class SysInfo implements Serializable {
    /**
     * 主机名称
     */
    private String hostName;
    /**
     * 主机地址
     */
    private String address;
    /**
     * 系统名称 如：Windows 10
     */
    private String osName;
    /**
     * 系统版本 如：10.0
     */
    private String osVersion;
    /**
     * 系统架构 如：amd64
     */
    private String osArch;
    /**
     * 系统用户名称
     */
    private String userName;
    /**
     * 运行目录
     */
    private String userDir;
    /**
     * 用户所在时区 如：Asia/Shanghai
     */
    private String userTimezone;

    public SysInfo() {
        this.hostName = IpUtils.getHostName();
        this.address = IpUtils.getHostAddress();
        this.osName = System.getProperty("os.name");
        this.osVersion = System.getProperty("os.version");
        this.osArch = System.getProperty("os.arch");
        this.userName = System.getProperty("user.name");
        this.userDir = System.getProperty("user.dir");
        this.userTimezone = System.getProperty("user.timezone");
    }
}