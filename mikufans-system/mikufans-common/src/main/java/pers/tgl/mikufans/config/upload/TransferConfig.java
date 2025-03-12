package pers.tgl.mikufans.config.upload;

import lombok.Data;
import pers.tgl.mikufans.consts.TransferMode;

@Data
public class TransferConfig {
    /**
     * 转存模式
     */
    private TransferMode mode = TransferMode.NONE;
    /**
     * 转存基本路径
     */
    private String path;
    /**
     * 转存成功后是否删除源文件
     */
    private boolean deleteOnSuccess = false;
}