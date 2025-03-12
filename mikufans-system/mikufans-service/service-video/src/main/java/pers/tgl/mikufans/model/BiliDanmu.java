package pers.tgl.mikufans.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 适用于B站XML格式弹幕
 * <d p="436.29100,1,25,16777215,1639850993,0,a1cf8z4,1684158506285864448,10">弹幕内容1</d>
 * <d p="795.73900,5,25,16777215,1639851409,0,ab833af4,1681761994974100736,10">弹幕内容2</d>
 */
@Data
public class BiliDanmu implements Serializable {
    /**
     * 出现时间,秒
     */
    private Float stime;
    /**
     * 弹幕模式 1-9,默认1
     * 1,2,3滚动弹幕
     * 4底部弹幕
     * 5顶部弹幕
     */
    private Integer mode;
    /**
     * 弹幕字号
     * 12 16 18 25 36 45 64
     */
    private Integer size;
    /**
     * 弹幕颜色,十进制,默认16777215
     */
    private Integer color;
    /**
     * 弹幕发送时间,unix时间戳
     */
    private Integer date;
    /**
     * 弹幕池 0 1 2 默认0
     */
    private Integer pool;
    /**
     * 弹幕发送者
     */
    private String uhash;
    /**
     * 弹幕id
     */
    private Long dmid;
    /**
     * 弹幕屏蔽等级0-11,低于用户设定的等级将会被屏蔽
     */
    private Integer weight;
    /**
     * 弹幕内容
     */
    private String text;
}