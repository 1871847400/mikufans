package pers.tgl.mikufans.entity;

import lombok.Data;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import pers.tgl.mikufans.util.FileSizeUtil;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class DiskInfo implements Serializable {
    private List<SysFile> sysFiles;

    public DiskInfo(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        sysFiles = new ArrayList<>(fsArray.size());
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setName(fs.getName());
            sysFile.setType(fs.getType());
            sysFile.setMount(fs.getMount());
            sysFile.setTotal(FileSizeUtil.formatFileSize(total));
            sysFile.setFree(FileSizeUtil.formatFileSize(free));
            sysFile.setUsed(FileSizeUtil.formatFileSize(used));
            sysFile.setUsage(new DecimalFormat("0.00%").format(used * 1.0D / total));
            sysFiles.add(sysFile);
        }
    }

    @Data
    public static class SysFile implements Serializable {
        private String name;
        private String type;
        private String mount;
        private String total;
        private String free;
        private String used;
        /**
         * 使用率 如：40.32%
         */
        private String usage;
    }
}