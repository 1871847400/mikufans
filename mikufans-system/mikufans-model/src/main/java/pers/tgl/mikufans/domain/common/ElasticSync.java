package pers.tgl.mikufans.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.BaseEntity;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class ElasticSync extends BaseEntity {
    /**
     * ES索引名称
     */
    private String idxName;
    /**
     * 同步任务名称
     */
    private String taskName;
    /**
     * 上次同步时间
     */
    private Date syncTime;
}