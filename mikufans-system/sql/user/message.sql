drop table if exists `user_whisper`;
create table `user_whisper`
(
    message     varchar(1024)    not null comment '发送内容',
    msg_type    tinyint unsigned not null comment '内容类型',
    read_flag   tinyint unsigned not null comment '是否已读',
    revoked     tinyint unsigned not null comment '是否撤回',
    receiver_id bigint unsigned  not null comment '接收用户id',
    index (receiver_id)
) comment '用户私信内容表';

# A发送B一封私信,A和B的私信列表都应该有出现对方
drop table if exists `user_dialog`;
create table `user_dialog`
(
    target_id bigint unsigned            not null comment '目标用户id',
    top       tinyint unsigned default 0 not null comment '是否置顶',
    last_id   bigint unsigned  default 0 not null comment '最后一条消息',
    unread    int unsigned     default 0 not null comment '未读消息数'
) comment '用户私信会话表';
create unique index uk_target_user on user_dialog (target_id, user_id, delete_flag);

drop table if exists `msg_unread`;
create table `msg_unread`
(
    whisper int unsigned default 0 not null comment '未读私信数',
    reply   int unsigned default 0 not null comment '未读回复数',
    atuser  int unsigned default 0 not null comment '未读AT数',
    likes   int unsigned default 0 not null comment '未读点赞数',
    systems int unsigned default 0 not null comment '未读系统消息数'
) comment '未读消息数量表';

drop table if exists `user_notice`;
create table `user_notice`
(
    notice_type int unsigned                not null comment '通知类型',
    notice_id   int unsigned                not null comment '通知id',
    params      varchar(512)                not null comment '模板内容参数',
    url         varchar(64)      default '' not null comment '自定义链接',
    read_flag   tinyint unsigned default 0  not null comment '是否已读',
    hidden      tinyint unsigned default 0  not null comment '是否隐藏'
) comment '用户通知表';

drop table if exists `search_history`;
create table `search_history`
(
    content      varchar(64)            not null comment '搜索内容',
    search_count int unsigned default 0 not null comment '搜索次数'
) comment '搜索记录表';
