package pers.tgl.mikufans.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchHistory extends UserBaseEntity {
    /**
     * 搜索内容
     */
    private String content;
    /**
     * 搜索次数
     */
    private Integer searchCount;
}