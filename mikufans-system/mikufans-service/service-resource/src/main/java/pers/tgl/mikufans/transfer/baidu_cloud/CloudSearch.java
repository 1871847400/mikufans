package pers.tgl.mikufans.transfer.baidu_cloud;

import lombok.Data;

import java.util.List;

@Data
public class CloudSearch {
    /**
     * 是否还有下一页
     */
    private Integer has_more;
    /**
     * 文件列表
     */
    private List<SearchFile> list;

    @Data
    public static class SearchFile {
        private Integer category; //文件类型
        private Long fs_id; //文件在云端的唯一标识
        private Integer isdir; //是否是目录，0为否，1为是
        private Long local_ctime;  //文件在客户端创建时间
        private Long local_mtime; //文件在客户端修改时间
        private Long server_ctime; //文件在服务端创建时间
        private Long server_mtime; //文件在服务端修改时间
        private String md5; //云端哈希（非文件真实MD5）
        private Long size; //文件大小
        private String thumbs; //缩略图地址
    }
}