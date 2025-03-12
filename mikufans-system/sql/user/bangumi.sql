drop table if exists `bangumi`;
create table `bangumi`
(
    video_id       bigint unsigned             not null comment '视频id',
    poster_id      bigint unsigned  default 0  not null comment '海报id',
    release_status tinyint unsigned default 0  not null comment '更新状态:0未知1更新中2完结',
    release_week   tinyint unsigned default 0  not null comment '每周更新日期:1,2,3',
    release_time   time                        null comment '具体更新时间HH:mm',
    release_season tinyint unsigned default 0  not null comment '播放季度:0未知1-4',
    subscribe      int unsigned     default 0  not null comment '订阅人数',
    rate           decimal(4, 1)    default 0  not null comment '综合评分',
    rate_count     int unsigned     default 0  not null comment '评分人数',
    premiere       date                        null comment '首映日期',
    staff          varchar(512)     default '' not null comment '工作人员',
    voice          varchar(512)     default '' not null comment '声优列表',
    official       varchar(512)     default '' not null comment '官方情报',
    series_tag     varchar(32)      default '' not null comment '系列标签:第一季',
    series_ids     varchar(512)     default '' not null comment '关联节目列表:第一季关联第二季',
    region_id      bigint unsigned  default 0  not null comment '地区id'
) comment '节目信息表';

drop table if exists `bangumi_subscribe`;
create table `bangumi_subscribe`
(
    bangumi_id bigint unsigned not null comment '节目id'
) comment '节目订阅表';
create unique index uk_bg_user on bangumi_subscribe (bangumi_id, user_id, delete_flag);

drop table if exists `bangumi_style_link`;
create table `bangumi_style_link`
(
    `bid` bigint unsigned not null comment '节目id',
    `sid` bigint unsigned not null comment '风格id'
) comment '节目风格关联表';
create unique index uk_bg_style on bangumi_style_link (bid, sid, delete_flag);
