# drop table if exists `music_group`;
# create table `music_group`
# (
#     `id`          bigint unsigned auto_increment not null comment '雪花id',
#     `user_id`     bigint unsigned                not null comment '用户id',
#     `group_name`  varchar(32) unique             not null comment '组名称',
#     `group_path`  varchar(256)                   not null comment '存放路径',
#     `sort_order`  float                          not null comment '排序值',
#     `size`        int unsigned                   not null comment '子项数量',
#     `create_time` datetime                       not null comment '创建时间',
#     `update_time` datetime                       not null comment '更新时间',
#     primary key (id)
# ) comment '音乐组表';
#
# drop table if exists `music_collect`;
# create table `music_collect`
# (
#     `id`          bigint unsigned auto_increment not null comment '雪花id',
#     `user_id`     bigint unsigned                not null comment '用户id',
#     `music_id`    varchar(32)                    not null comment '音乐id',
#     `group_id`    bigint unsigned                not null comment '组id',
#     `sort_order`  float                          not null comment '排序值',
#     `music_name`  varchar(64)                    not null comment '音乐名称',
#     `privilege`   int                            not null comment '特权',
#     `platform`    int                            not null comment '音乐平台',
#     `details`     varchar(2048)                  not null comment '详细数据',
#     `create_time` datetime                       not null comment '创建时间',
#     primary key (id),
#     unique key (user_id, music_id, group_id)
# ) comment '用户收藏音乐表';
