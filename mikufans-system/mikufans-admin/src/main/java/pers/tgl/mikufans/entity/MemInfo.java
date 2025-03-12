package pers.tgl.mikufans.entity;

import lombok.Data;
import oshi.hardware.GlobalMemory;
import pers.tgl.mikufans.util.FileSizeUtil;

import java.io.Serializable;
import java.text.DecimalFormat;

@Data
public class MemInfo implements Serializable {
    /**
     * 内存总量
     */
    private String total;
    /**
     * 已用内存
     */
    private String used;
    /**
     * 剩余内存
     */
    private String free;
    /**
     * 使用率
     */
    private String usage;

    public MemInfo(GlobalMemory memory) {
        this.total = FileSizeUtil.formatFileSize(memory.getTotal());
        this.used = FileSizeUtil.formatFileSize(memory.getTotal() - memory.getAvailable());
        this.free = FileSizeUtil.formatFileSize(memory.getAvailable());
        this.usage = new DecimalFormat("0.00%").format((memory.getTotal() - memory.getAvailable()) * 1.0D / memory.getTotal());
    }
}