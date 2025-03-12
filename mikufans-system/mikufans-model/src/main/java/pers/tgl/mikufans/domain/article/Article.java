package pers.tgl.mikufans.domain.article;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;
import pers.tgl.mikufans.domain.base.UserBaseEntity;
import pers.tgl.mikufans.validator.group.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(autoResultMap = true)
public class Article extends UserBaseEntity {
    /**
     * 文章分类
     */
    @NotNull(message = "请选择文章分类", groups = Create.class)
    private Long channelId;
    /**
     * 专栏id
     */
    private Long colId;
    /**
     * 文章标题
     */
    @NotBlank(message = "请输入文章标题")
    @Length(max = 24, message = "标题最多24个字")
    private String title;
    /**
     * 文章内容(包含样式)
     */
    @TableField(select = false)
    @NotBlank(message = "请输入文章内容")
    @Length(max = 20000, message = "文章内容达到上限")
    private String content;
    /**
     * 文章封面id
     */
    private Long bannerId;
    /**
     * 文章字数
     */
    private Integer words;
    /**
     * 阅读次数
     */
    private Integer views;
    /**
     * 投币数量
     */
    private Integer coins;
    /**
     * 收藏次数
     */
    private Integer stars;
    /**
     * 分享次数
     */
    private Integer shares;
    /**
     * 热度分数
     */
    private BigDecimal score;
    /**
     * 自定义标签列表
     */
    @UniqueElements(message = "标签名称不能重复")
    @Size(max = 10, message = "最多添加10个标签")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<@Length(max = 16, message = "每个标签最多16个字") @NotBlank(message = "标签名称不能为空") String> tags;
}