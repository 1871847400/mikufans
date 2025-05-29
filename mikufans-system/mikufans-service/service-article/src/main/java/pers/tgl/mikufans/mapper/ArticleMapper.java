package pers.tgl.mikufans.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.tgl.mikufans.domain.article.Article;

@Mapper
public interface ArticleMapper extends MPJBaseMapper<Article> {
}
