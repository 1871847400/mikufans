package pers.tgl.mikufans.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.tgl.mikufans.anno.PermFlag;
import pers.tgl.mikufans.anno.PermGroup;
import pers.tgl.mikufans.domain.base.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@PermFlag(name = "命令日志", group = PermGroup.LOG)
public class CmdLog extends BaseEntity {
    /**
     * 执行的指令
     */
    private String cmd;
    /**
     * 输出的日志
     */
    private String output;
    /**
     * 如果执行失败了,原因
     */
    private String errorMsg;
    /**
     * 执行结果 (子进程退出码)
     */
    private Integer exitCode;
}