package pers.tgl.mikufans.transfer.aliyun_oss;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.config.OssConfig;
import pers.tgl.mikufans.transfer.ResourceTransfer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OssUtils implements ResourceTransfer, InitializingBean {
    private final OssConfig ossConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StrUtil.isBlank(ossConfig.getAccessKeyId()) || StrUtil.isBlank(ossConfig.getAccessKeySecret())) {
            return;
        }
        String bucketName = ossConfig.getBucketName();
        OSS ossClient = getOSSClient();
        try {
            boolean exists = ossClient.doesBucketExist(bucketName);
            //如果bucket不存在则自动创建
            if (!exists) {
                try {
                    ossClient.createBucket(bucketName);
                    ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
                    log.info("创建bucket={}成功", bucketName);
                } catch (ClientException | OSSException e) {
                    log.error("自动创建bucket失败", e);
                }
            }
            ossClient.shutdown();
        } catch (Exception e) {
            log.error("连接OSS异常", e);
        }
    }

    private OSS getOSSClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret()
        );
        // 创建OSSClient实例。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        return OSSClientBuilder.create()
                .endpoint(ossConfig.getEndpoint())
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(ossConfig.getRegion())
                .build();
    }

    @Override
    public void upload(File file, String filePath) throws IOException {
//         填写Bucket名称，例如examplebucket。
//        String bucketName = ossConfig.getBucketName();
//         填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
//        String objectName = "exampledir/exampleobject.txt";
//         待上传本地文件路径。
//        String filePath = "D:\\localpath\\examplefile.txt";
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        OSS ossClient = getOSSClient();
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossConfig.getBucketName(), filePath, file);
            ossClient.putObject(putObjectRequest);
        } catch (ClientException | OSSException e) {
            log.error("oss upload error", e);
            throw new IOException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public List<String> getDownloadLinks(String filePath) {
        String url = "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + filePath;
        return Collections.singletonList(url);
    }

    @Override
    public void delete(String ...filePaths) throws IOException {
        if (ArrayUtil.isEmpty(filePaths)) {
            return;
        }
        OSS ossClient = getOSSClient();
        try {
            for (String filePath : filePaths) {
                if (filePath.startsWith("/")) {
                    filePath = filePath.substring(1);
                }
                //删除文件或目录,如果是目录必须是空目录
                ossClient.deleteObject(ossConfig.getBucketName(), filePath);
            }
        } catch (ClientException | OSSException e) {
            log.error("oss delete error", e);
            throw new IOException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
}