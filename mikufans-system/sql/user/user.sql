drop table if exists `user_star`;
create table `user_star`
(
    star_name  varchar(32)                  not null comment '收藏夹名称',
    star_count smallint          default 0  not null comment '收藏数量',
    def_flag   tinyint unsigned  default 0  not null comment '默认收藏夹0否1是',
    cover_id   bigint unsigned   default 0  not null comment '收藏夹封面',
    intro      varchar(100)      default '' not null comment '收藏夹简介',
    visible    tinyint unsigned  default 1  not null comment '是否公开',
    sort       smallint unsigned default 1  not null comment '排序'
) comment '用户收藏夹表';

drop table if exists `user_follow`;
create table `user_follow`
(
    target_id    bigint unsigned            not null comment '关注用户id',
    access_count int unsigned default 0     not null comment '访问次数',
    access_time  datetime     default now() not null comment '上次访问时间'
) comment '用户关注表';
create unique index uk_user_target on user_follow (user_id, target_id, delete_flag);

drop table if exists `user_flag`;
create table `user_flag`
(
    flag_key   varchar(32)  not null comment '标志键',
    flag_value varchar(128) not null comment '标志值'
) comment '用户标志表';

drop table if exists `user_favor`;
create table `user_favor`
(
    favor_type tinyint unsigned not null comment '爱好类型',
    tag        varchar(32)      not null comment '标签'
) comment '用户爱好表';

drop table if exists `user_like`;
create table `user_like`
(
    like_data_id bigint unsigned  not null comment '点赞数据id',
    like_val     tinyint unsigned not null comment '点赞值',
    index (like_data_id)
) comment '用户点赞表';

drop table if exists `user_like_data`;
create table `user_like_data`
(
    like_type     tinyint unsigned               not null comment '点赞类型',
    busi_id       bigint unsigned                not null comment '业务id',
    likes         int unsigned     default 0     not null comment '点赞数量',
    dislikes      int unsigned     default 0     not null comment '点踩数量',
    like_user_ids varchar(100)                   not null comment '最新点赞用户',
    read_flag     tinyint unsigned               not null comment '是否已读',
    hidden        tinyint unsigned default 0     not null comment '忽略通知',
    like_time     datetime         default now() not null comment '最近点赞时间'
) comment '用户点赞统计表';

drop table if exists `user_album`;
create table `user_album`
(
    pid      bigint unsigned default 0 not null comment '所在相册id',
    category tinyint unsigned          not null comment '类型,1图片2相册',
    title    varchar(64)               not null comment '名称',
    img_id   bigint unsigned           not null comment '图片id',
    sort     int unsigned    default 1 not null comment '排序',
    index (pid)
) comment '用户相册表';

drop table if exists `user_publish`;
create table `user_publish`
(
    title   varchar(32)   not null comment '说说标题',
    content varchar(2048) not null comment '说说内容',
    img_ids varchar(512)  not null comment '图片id列表'
) comment '用户说说表';

drop table if exists `user_rate`;
create table `user_rate`
(
    target_id bigint unsigned          not null comment '目标id',
#     target_type  tinyint unsigned         not null comment '目标类型',
    rate      tinyint unsigned         not null comment '评分值',
    content   varchar(1024) default '' not null comment '评价内容'
) comment '用户点评表';
create unique index uk_target on user_rate (target_id, user_id, delete_flag);

drop table if exists `user_dynamic`;
create table `user_dynamic`
(
    share_id     bigint unsigned  default 0     not null comment '转发id',
    share_reason varchar(1024)    default ''    not null comment '转发理由',
    shares       int unsigned     default 0     not null comment '转发次数',
    target_id    bigint unsigned                not null comment '目标id,动态来源',
    dynamic_type tinyint unsigned               not null comment '动态类型',
    top          tinyint unsigned default 0     not null comment '是否置顶0否1是',
    visible      tinyint unsigned default 1     not null comment '可见范围',
    publish_flag tinyint unsigned default 0     not null comment '是否定时发布',
    publish_time datetime         default now() not null comment '发布时间',
    disabled     tinyint unsigned default 0     not null comment '是否禁用',
    index (share_id),
    index (target_id, dynamic_type)
) comment '用户动态表';

drop table if exists `user_oper_log`;
create table `user_oper_log`
(
    title       varchar(32)                 not null comment '操作标题',
    username    varchar(100)     default '' not null comment '用户名',
    ipaddr      varchar(100)     default '' not null comment 'IP地址',
    client_type tinyint unsigned default 0  not null comment '客户端类型0未知,1:PC,2:APP',
    user_agent  varchar(128)     default '' not null comment '操作代理',
    oper_status tinyint unsigned            not null comment '操作状态0成功1失败',
    oper_type   tinyint unsigned            not null comment '操作类型',
    req_uri     varchar(1024)               not null comment '请求资源路径',
    req_params  varchar(1024)    default '' not null comment '请求参数',
    req_method  varchar(10)                 not null comment '请求方式GET,POST等',
    method_path varchar(512)                not null comment '请求方法路径',
    cost_time   int unsigned                not null comment '耗费时间(毫秒)',
    user_type   tinyint unsigned            not null comment '用户类型'
) comment '用户操作日志表';

drop table if exists `user_report`;
create table `user_report`
(
    target_id   bigint unsigned             not null comment '举报业务id',
    report_type tinyint unsigned            not null comment '举报业务类型',
    behavior_id int unsigned                not null comment '举报行为id',
    reason      varchar(512)     default '' not null comment '详细理由',
    audit       tinyint unsigned default 0  not null comment '审核状态',
    index (target_id)
) comment '用户举报表';