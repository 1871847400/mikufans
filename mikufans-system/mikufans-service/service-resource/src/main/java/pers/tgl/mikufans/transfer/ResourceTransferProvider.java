package pers.tgl.mikufans.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pers.tgl.mikufans.consts.TransferMode;
import pers.tgl.mikufans.transfer.baidu_cloud.CloudUtils;
import pers.tgl.mikufans.transfer.aliyun_oss.OssUtils;

@Component
@RequiredArgsConstructor
public class ResourceTransferProvider {
    private final CloudUtils cloudUtils;
    private final OssUtils ossUtils;

    public ResourceTransfer getResourceTransfer(TransferMode mode) {
        switch (mode) {
            case CLOUD:
                return cloudUtils;
            case OSS:
                return ossUtils;
            default:
                return null;
        }
    }
}