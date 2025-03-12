package pers.tgl.mikufans.entity;

import lombok.Data;
import oshi.hardware.CentralProcessor;
import oshi.util.Util;

import java.io.Serializable;
import java.text.DecimalFormat;

@Data
public class CpuInfo implements Serializable {
    /**
     * 核心数
     */
    private int cpuNum;
    /**
     * CPU系统使用率
     */
    private String sys;
    /**
     * CPU用户使用率
     */
    private String used;
    /**
     * CPU当前等待率
     */
    private String wait;
    /**
     * CPU当前空闲率
     */
    private String free;

    public CpuInfo(CentralProcessor processor) {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        //睡眠1s
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        this.cpuNum = processor.getLogicalProcessorCount();
        this.sys = new DecimalFormat("0.00%").format(cSys * 1.0 / totalCpu);
        this.used = new DecimalFormat("0.00%").format(user * 1.0 / totalCpu);
        this.wait = new DecimalFormat("0.00%").format(iowait * 1.0 / totalCpu);
        this.free = new DecimalFormat("0.00%").format(idle * 1.0 / totalCpu);
    }
}