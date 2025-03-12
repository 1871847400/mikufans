package pers.tgl.mikufans.vo;

import lombok.Data;

@Data
public class EmailSendResult {
    private String uuid;
    /**
     * 失效时间(s)
     */
    private Integer timeout;
    /**
     * 邮件发送间隔(s)
     */
    private Integer interval;
}