package pers.tgl.mikufans.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import pers.tgl.mikufans.domain.enums.DanmuType;
import pers.tgl.mikufans.domain.video.VideoPart;
import pers.tgl.mikufans.validator.db.DBExists;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class VideoDanmuDto implements Serializable {
    @NotNull
    @DBExists(VideoPart.class)
    private Long partId;
    /**
     * 弹幕内容
     */
    @NotBlank(message = "弹幕内容不能为空")
    @Length(min = 1, max = 32, message = "弹幕长度1-32")
    private String content;
    /**
     * 颜色代码,必须类似于: #ffffff #fff
     */
    @NotBlank
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "颜色代码格式不正确")
    private String fontColor;
    /**
     * 字体大小
     */
    @NotNull
    @Range(min = 1, max = 1)
    private Integer fontType;
    /**
     * 发送时间,秒
     */
    @NotNull
    @Range(min = 0, max = 99999999)
    private BigDecimal sendTime;
    /**
     * 弹幕类型
     */
    @NotNull
    private DanmuType danmuType;
}