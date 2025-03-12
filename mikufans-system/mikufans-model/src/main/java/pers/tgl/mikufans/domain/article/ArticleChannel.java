package pers.tgl.mikufans.domain.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.SystemBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleChannel extends SystemBaseEntity {
    private String title;
    private Integer sort;
    private Integer total;
}
