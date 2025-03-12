drop table if exists `article_column`;
create table `article_column`
(
    title     varchar(24)                  not null comment '专栏标题',
    intro     varchar(128)      default '' not null comment '专栏描述',
    poster_id bigint unsigned   default 0  not null comment '专栏海报图',
    articles  smallint unsigned default 0  not null comment '文章数量',
    views     int unsigned      default 0  not null comment '阅读总数',
    words     int unsigned      default 0  not null comment '总字数'
) comment '用户文章专栏表';

drop table if exists `article`;
create table `article`
(
    channel_id int unsigned               not null comment '文章分类id',
    col_id     bigint unsigned default 0  not null comment '专栏id',
    title      varchar(24)                not null comment '文章标题',
    content    text                       not null comment '文章内容',
    banner_id  bigint unsigned default 0  not null comment '封面id',
    words      int unsigned    default 0  not null comment '文章字数',
    views      int unsigned    default 0  not null comment '阅读数量',
    coins      int unsigned    default 0  not null comment '投币数量',
    stars      int unsigned    default 0  not null comment '收藏数量',
    shares     int unsigned    default 0  not null comment '转发数量',
    score      decimal(10, 2)  default 0  not null comment '热度分数',
    tags       varchar(200)    default '' not null comment '自定义标签列表',
    index (channel_id),
    index (col_id)
) comment '用户文章表';