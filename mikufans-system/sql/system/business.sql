# perms直接使用perm_key作为项,所以不会响应变化
drop table if exists `user`;
create table `user`
(
    username      varchar(128)                          not null comment '用户名(一般为邮箱)',
    nickname      varchar(16)                           not null comment '用户昵称',
    password      varchar(255)                          not null comment '用户密码',
    gender        tinyint unsigned        default 0     not null comment '性别,0保密1男2女',
    birthday      date                                  null comment '出生日期',
    sign          varchar(200)            default ''    not null comment '个性签名',
    avatar_id     bigint unsigned         default 0     not null comment '头像id',
    user_level    tinyint unsigned        default 0     not null comment '等级',
    exp           int unsigned            default 0     not null comment '经验值',
    coin          decimal(10, 1) unsigned default 0     not null comment '硬币数量',
    background    varchar(32)             default ''    not null comment '背景图片',
    disabled      tinyint unsigned        default 0     not null comment '0正常1禁止登录',
    register_time datetime                default now() not null comment '注册时间',
    perms         varchar(512)                          not null comment '权限列表',
    likes         int unsigned            default 0     not null comment '被点赞数量',
    dislikes      int unsigned            default 0     not null comment '被点踩数量',
    coins         int unsigned            default 0     not null comment '被投币数量',
    videos        int unsigned            default 0     not null comment '投稿视频数量',
    follows       int unsigned            default 0     not null comment '关注数量',
    fans          int unsigned            default 0     not null comment '粉丝数量',
    subscribes    int unsigned            default 0     not null comment '订阅数量',
    publishes     int unsigned            default 0     not null comment '说说数量',
    dynamics      int unsigned            default 0     not null comment '动态数量',
    articles      int unsigned            default 0     not null comment '文章数量',
    unique (username),
    #全文索引,方便通过昵称模糊查找用户
    fulltext (nickname) with parser ngram
) comment '用户信息表';

drop table if exists `user_perm`;
create table `user_perm`
(
    perm_key  varchar(64) not null comment '权限关键字',
    perm_name varchar(32) not null comment '权限名称'
) comment '用户权限表';
create unique index uk_perm_key on user_perm (perm_key, delete_flag);

drop table if exists `report_behavior`;
create table `report_behavior`
(
    category varchar(32)             not null comment '举报类别',
    behavior varchar(32)             not null comment '举报行为',
    intro    varchar(128) default '' not null comment '行为介绍'
) comment '举报行为配置表';

drop table if exists `video_channel`;
create table `video_channel`
(
    pid          int unsigned     default 0  not null comment '父分区id',
    channel_name varchar(32)                 not null comment '分区名称',
    channel_desc varchar(255)     default '' not null comment '分区描述',
    icon_name    varchar(32)      default '' not null comment '图标名称',
    url          varchar(255)     default '' not null comment '跳转路径',
    disabled     tinyint unsigned default 0  not null comment '是否禁用0否1是',
    sort         int              default 1  not null comment '排序值',
    index (pid)
) comment '视频分区表';

drop table if exists `video_danmu_color`;
create table `video_danmu_color`
(
    color_name varchar(16)                   not null comment '颜色名称',
    color_code varchar(12)                   not null comment '颜色代码',
    disabled   tinyint(1) unsigned default 0 not null comment '是否禁用'
) comment '弹幕颜色配置表';

drop table if exists `bangumi_style`;
create table `bangumi_style`
(
    video_type tinyint unsigned not null comment '视频类型',
    style_name varchar(32)      not null comment '风格名称'
) comment '节目风格表';

drop table if exists `article_channel`;
create table `article_channel`
(
    title varchar(16)                 not null comment '分类名称',
    sort  smallint unsigned default 1 not null comment '排序',
    total int unsigned      default 0 not null comment '总投稿数量'
) comment '文章分类表';