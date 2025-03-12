package pers.tgl.mikufans.domain.bangumi;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("bangumi_subscribe")
public class BangumiSubscribe extends UserBaseEntity {
    /**
     * 关联的节目id
     */
    private Long bangumiId;
    /**
     * 订阅时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
//    @JsonProperty("subscribe_time")
    public Date getSubscribeDate() {
        return getCreateTime();
    }
}