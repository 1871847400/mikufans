drop table if exists `video`;
create table `video`
(
    sid         char(6)                      not null comment '短id',
    uid         int unsigned                 not null comment '创建序号',
    `type`      tinyint unsigned             not null comment '视频类型',
    channel_id  bigint unsigned              not null comment '分区id',
    title       varchar(128)                 not null comment '视频标题',
    intro       varchar(2048)     default '' not null comment '视频简介',
    tags        varchar(255)      default '' not null comment '标签列表',
    republish   tinyint unsigned  default 0  not null comment '是否为转载',
    source_url  varchar(200)      default '' not null comment '转载地址',
    search      tinyint unsigned  default 0  not null comment '是否可被搜到',
    user_level  smallint unsigned default 0  not null comment '观看等级',
    banner_id   bigint unsigned   default 0  not null comment '电脑端封面id',
    m_banner_id bigint unsigned   default 0  not null comment '手机端封面id',
    duration    int unsigned      default 0  not null comment '视频总时长(毫秒)',
    bangumi_id  bigint unsigned   default 0  not null comment '关联节目id',
    score       decimal(10, 2)    default 0  not null comment '热度分数',
    plays       int unsigned      default 0  not null comment '播放数量',
    coins       int unsigned      default 0  not null comment '投币数量',
    stars       int unsigned      default 0  not null comment '收藏数量',
    danmus      int unsigned      default 0  not null comment '弹幕数量',
    shares      int unsigned      default 0  not null comment '分享次数',
    disabled    tinyint unsigned  default 0  not null comment '是否禁用',
    reason      varchar(64)       default '' not null comment '禁用原因',
    apply       tinyint unsigned  default 0  not null comment '重审次数',
    unique (sid),
    unique (uid),
    index (channel_id),
    # 创建全文索引,默认分词长度为2,可以在配置文件中改为1
    fulltext (title) with parser ngram
) comment '视频表';

drop table if exists `video_part`;
create table `video_part`
(
    video_id  bigint unsigned            not null comment '视频id',
    video_sid char(6)                    not null comment '视频sid',
    res_id    bigint unsigned  default 0 not null comment '资源id',
    part_type tinyint unsigned           not null comment '分集类型',
    part_name varchar(32)                not null comment '分集名称',
    sort      smallint unsigned          not null comment '排序值',
    banner_id bigint unsigned  default 0 not null comment '封面id',
    disabled  tinyint unsigned default 0 not null comment '是否禁用0否1是',
    danmus    int unsigned     default 0 not null comment '弹幕数量',
    append    tinyint unsigned default 1 not null comment '是否为追加分集0否1是',
    canplay   tinyint unsigned default 0 not null comment '能否播放',
    index (video_id),
    index (video_sid),
    index (res_id)
) comment '视频分集表';

drop table if exists `video_resource`;
create table `video_resource`
(
    md5           char(32)         not null comment 'MD5',
    media_type    varchar(64)      not null comment '媒体类型',
    file_size     int unsigned     not null comment '文件大小',
    duration      int unsigned     not null comment '视频时长,毫秒',
    local_path    varchar(255)     not null comment '本地存储路径',
    transfer_mode tinyint unsigned not null comment '转存模式',
    transfer_path varchar(255)     not null comment '转存路径',
    pending       tinyint unsigned not null comment '是否可用0可用1不可用',
    media_info    text             not null comment 'ffprobe'
) comment '视频资源表';

drop table if exists `video_subtitle`;
create table `video_subtitle`
(
    `rid`       bigint unsigned not null comment '资源id',
    `vid`       bigint unsigned not null comment '视频id',
    `type`      varchar(16)     not null comment '字幕类型',
    `region_id` bigint unsigned not null comment '地区语言id',
    `filename`  varchar(64)     not null comment '文件名称',
    save_name   varchar(64)     not null comment '保存名称'
) comment '视频字幕表';

drop table if exists `video_process`;
create table `video_process`
(
    video_id  bigint unsigned             not null comment '视频id',
    part_id   bigint unsigned             not null comment '分集id',
    res_id    bigint unsigned             not null comment '资源id',
    filepath  varchar(128)                not null comment '视频文件路径',
    progress  tinyint unsigned default 0  not null comment '进度(0-100)',
    stage     tinyint unsigned            not null comment '阶段',
    params    mediumtext                  not null comment '加工参数(json)',
    result    tinyint unsigned default 0  not null comment '结果0等待1成功2失败',
    error_msg varchar(128)     default '' not null comment '失败的原因',
    index (video_id),
    index (part_id),
    index (res_id)
) comment '视频加工表';

drop table if exists `video_watch_history`;
create table `video_watch_history`
(
    video_id   bigint unsigned  not null comment '视频id',
    part_id    bigint unsigned  not null comment '分集id',
    watch_time int unsigned     not null comment '观看时长',
    watch_pos  int unsigned     not null comment '观看位置',
    device     tinyint unsigned not null comment '观看设备',
    index (video_id, part_id)
) comment '视频观看历史表';
create unique index uk_user_video on video_watch_history (user_id, video_id, delete_flag);

drop table if exists `video_danmu`;
create table `video_danmu`
(
    video_id   bigint unsigned  not null comment '视频id',
    part_id    bigint unsigned  not null comment '分集id',
    content    varchar(128)     not null comment '弹幕内容',
    font_color varchar(10)      not null comment '颜色代码',
    font_type  tinyint unsigned not null comment '字体类型',
    send_time  decimal(10, 2)   not null comment '发送时间',
    danmu_type tinyint unsigned not null comment '弹幕类型',
    index (video_id, part_id)
) comment '视频弹幕表';

drop table if exists `video_star`;
create table `video_star`
(
    video_id bigint unsigned not null comment '视频id',
    star_id  bigint unsigned not null comment '收藏夹id'
) comment '视频收藏表';
create unique index uk_video_star on video_star (star_id, video_id, delete_flag);

drop table if exists `video_coin`;
create table `video_coin`
(
    video_id   bigint unsigned  not null comment '视频id',
    coin_count tinyint unsigned not null comment '投币数量',
    index (video_id)
) comment '视频投币表';

drop table if exists `video_flag`;
create table `video_flag`
(
    video_id   bigint unsigned           not null comment '视频id',
    part_id    bigint unsigned default 0 not null comment '分集id',
    flag_name  varchar(64)               not null comment '标志名称(允许重复)',
    flag_value varchar(128)              not null comment '标志值',
    index (video_id, part_id)
) comment '视频标志表';

drop table if exists `video_represent`;
create table `video_represent`
(
    video_id bigint unsigned         not null comment '视频id',
    sort     int unsigned default 1  not null comment '排序',
    reason   varchar(512) default '' not null comment '原因'
) comment '用户代表视频';
create unique index uk_video_user on video_represent (video_id, user_id, delete_flag);

drop table if exists `video_share`;
create table `video_share`
(
    share_code  varchar(16)            not null comment '分享码',
    video_id    bigint unsigned        not null comment '视频id',
    share_count int unsigned default 0 not null comment '分享成功次数',
    index (video_id)
) comment '视频分享表';
