drop table if exists `cmd_log`;
create table `cmd_log`
(
    cmd       varchar(1024)           not null comment '执行的指令',
    output    mediumtext              not null comment '输出日志',
    error_msg varchar(255) default '' not null comment '异常信息',
    exit_code tinyint                 not null comment '退出代码'
) comment '执行命令的日志表';

drop table if exists `elastic_sync`;
create table `elastic_sync`
(
    idx_name  varchar(64) not null comment 'ES索引名称',
    task_name varchar(64) not null comment '任务名称',
    sync_time datetime    not null comment '上次同步时间'
) comment 'es同步表';