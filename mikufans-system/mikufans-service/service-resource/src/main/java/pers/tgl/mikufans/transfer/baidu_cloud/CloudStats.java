package pers.tgl.mikufans.transfer.baidu_cloud;

import lombok.Data;

@Data
public class CloudStats {
    /**
     * 总空间大小，单位B
     */
    private Long total;
    /**
     * 7天内是否有容量到期
     */
    private Boolean expire;
    /**
     * 已使用大小，单位B
     */
    private Long used;
    /**
     * 免费容量，单位B
     */
    private Long free;
}
