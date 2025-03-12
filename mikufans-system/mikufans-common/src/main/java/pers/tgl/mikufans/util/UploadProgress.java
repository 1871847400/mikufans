package pers.tgl.mikufans.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadProgress {
    /**
     * 总大小
     */
    private long total;
    /**
     * 已上传的大小
     */
    private long upload;
    /**
     * 当前上传的大小
     */
    private long current;
}
