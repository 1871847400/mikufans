package pers.tgl.mikufans.consts;

public enum VideoStatus {
    ANY,
    /**
     * 包含处理中,审核中的part
     */
    DOING,
    /**
     * 全部part都完成
     */
    SUCCESS,
    /**
     * 视频被封禁,part处理失败,审核不通过
     */
    FAIL
}