package pers.tgl.mikufans.transfer;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ResourceTransfer {
    /**
     * 转存文件到第三方平台
     * @param file 需要上传的文件
     * @param savePath 保存文件的完整路径
     */
    void upload(File file, String savePath) throws IOException;

    /**
     * 获取文件资源的下载链接
     */
    List<String> getDownloadLinks(String filePath);

    /**
     * 删除文件或目录
     */
    void delete(String ...filePaths) throws IOException;
}