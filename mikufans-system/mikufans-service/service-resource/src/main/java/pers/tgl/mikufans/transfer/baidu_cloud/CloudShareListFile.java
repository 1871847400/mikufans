package pers.tgl.mikufans.transfer.baidu_cloud;

import lombok.Data;

import java.util.Map;

/**
 * 获取分享链接的文件列表  中的文件或文件夹
 */
@Data
public class CloudShareListFile {
    private Integer category;
    /**
     * 文件夹为NULL
     */
    private Long fs_id;
    /**
     * 0否 1是
     */
    private Integer isdir;
    /**
     * 文件的md5  如果是文件夹则为null
     */
    private String md5;
    /**
     * 该文件的完整路径  /xxxx/xxxx/1.txt
     */
    private String path;
    /**
     * 文件名 1.txt
     */
    private String server_filename;
    /**
     * 文件大小 如果是文件夹则为0
     */
    private Long size;
    /**
     * key: url
     */
    private Map<String, String> thumbs;
    /**
     * 链接 但是直接访问报错
     */
    private String docpreview;
    /**
     * 空串
     */
    private String newdocpreview;
    /**
     * 文档预览地址
     */
    private String picdocpreview;
}