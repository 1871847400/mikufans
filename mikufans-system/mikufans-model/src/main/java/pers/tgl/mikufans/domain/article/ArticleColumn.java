package pers.tgl.mikufans.domain.article;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.domain.base.UserBaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleColumn extends UserBaseEntity {
    private String title;
    private String intro;
    private Long posterId;
    private Integer articles;
    private Integer views;
    private Integer words;
}