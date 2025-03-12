package pers.tgl.mikufans.util;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * 文件大小工具类
 * <div>数据存储转换，最大支持到G</div>
 * @author xzbd
 *
 */
public class FileSizeUtil {
    public static final Integer K_SIZE = 1024;

    public static final Integer M_SIZE = 1048576;

    public static final Integer G_SIZE = 1073741824;

    public static final String B = "B";

    public static final String K = "K";

    public static final String M = "M";

    public static final String G = "G";

    /**
     * 将字节数据转为带单位的字符串
     *
     * @param fileSize 文件字节大小
     * @return
     */
    public static String formatFileSize(Long fileSize) {
        String fileSizeStr = "";
        if (Objects.isNull(fileSize)) {
            return fileSizeStr;
        }
        if (fileSize == 0L) {
            return "0".concat(B);
        }
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileSize < K_SIZE) {
            fileSizeStr = df.format((double) fileSize) + B;
        } else if (fileSize < M_SIZE) {
            fileSizeStr = df.format((double) fileSize / K_SIZE) + K;
        } else if (fileSize < G_SIZE) {
            fileSizeStr = df.format((double) fileSize / M_SIZE) + M;
        } else {
            fileSizeStr = df.format((double) fileSize / G_SIZE) + G;
        }
        return fileSizeStr;
    }
}
