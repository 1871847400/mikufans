drop table if exists `user_comment_area`;
create table `user_comment_area`
(
    comment_flag   tinyint unsigned default 0 not null comment '评论设置',
    user_level     tinyint unsigned default 0 not null comment '评论最低等级',
    comments       int unsigned     default 0 not null comment '评论总数',
    new_comment_id bigint unsigned  default 0 not null comment '最新评论id',
    hot_comment_id bigint unsigned  default 0 not null comment '最热评论id',
    fst_comment_id bigint unsigned  default 0 not null comment '最早评论id',
    top_comment_id bigint unsigned  default 0 not null comment '置顶评论id',
    busi_type      tinyint unsigned           not null comment '业务类型',
    busi_id        bigint unsigned            not null comment '业务id'
) comment '评论区表';

#回复的评论id: 如果不为0代表这是子评论,最多只会出现2级评论
#回复的用户id: 1.评论目标的作者 2.父评论的作者 3.其它子评论的作者
drop table if exists `user_comment`;
create table `user_comment`
(
    area_id       bigint unsigned   default 0 not null comment '评论区id',
    uid           int unsigned                not null comment '楼层',
    pid           bigint unsigned   default 0 not null comment '父评论id',
    reply_id      bigint unsigned   default 0 not null comment '回复的评论id',
    reply_user_id bigint unsigned             not null comment '回复的用户id',
    at_users      varchar(512)                not null comment 'at用户列表',
    image_ids     varchar(255)                not null comment '图片id列表',
    child_count   smallint unsigned default 0 not null comment '子评论数量',
    content       varchar(1024)               not null comment '评论内容',
    top           tinyint unsigned  default 0 not null comment '是否置顶',
    score         decimal(10, 2)    default 0 not null comment '热度分数',
    selected      tinyint unsigned  default 0 not null comment '是否为精选评论',
    index (area_id, reply_id),
    index (area_id, pid),
    index (reply_user_id)
) comment '用户评论表';

drop table if exists `user_comment_msg`;
create table `user_comment_msg`
(
    comment_id bigint unsigned            not null comment '评论id',
    msg_type   tinyint unsigned           not null comment '消息类型',
    read_flag  tinyint unsigned default 0 not null comment '是否已读',
    index (comment_id)
) comment '评论消息表';