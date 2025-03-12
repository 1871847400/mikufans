drop table if exists `image_resource`;
create table `image_resource`
(
    `md5`           char(32)                not null comment 'MD5',
    `local_path`    varchar(255) default '' not null comment '本地保存地址',
    `transfer_path` varchar(255) default '' not null comment '转存地址',
    `transfer_mode` tinyint unsigned        not null comment '转存模式',
    `media_type`    varchar(128)            not null comment '媒体类型',
    `file_size`     int unsigned            not null comment '文件大小(字节)',
    `main_color`    varchar(10)  default '' not null comment '主色调(颜色代码)',
    `resolution`    varchar(20)  default '' not null comment '图片分辨率',
    index (md5)
) comment '图片资源表';

drop table if exists `upload_task`;
create table `upload_task`
(
    md5           char(32)           not null comment 'MD5',
    media_type    varchar(100)       not null comment '媒体类型',
    file_name     varchar(255)       not null comment '文件原始名称',
    file_size     int unsigned       not null comment '文件大小(字节)',
    file_path     varchar(255)       not null comment '保存位置',
    upload_status tinyint unsigned   not null comment '上传状态',
    chunk_code    smallint unsigned  not null comment '当前分片号',
    chunk_size    mediumint unsigned not null comment '分片大小(字节)',
    chunk_count   smallint unsigned  not null comment '分片数量',
    upload_size   int unsigned       not null comment '已上传大小(字节)',
    expire_at     datetime           not null comment '到期时间',
    removed       tinyint unsigned   not null comment '是否已删除源文件',
    index (md5)
) comment '上传任务表';

# drop table if exists `music_resource`;
# create table `music_resource`
# (
#     id          bigint unsigned  not null comment '雪花id',
#     `filename`    varchar(128)     not null comment '文件名',
#     `hash_str`    varchar(64)      not null comment '哈希值',
#     `music_id`    varchar(64)      null comment '音乐id',
#     `quality`     smallint         not null comment '音质',
#     `size`        bigint unsigned  not null comment '文件大小',
#     `m3u8`        text             null     comment '索引内容',
#     `user_id`     bigint unsigned  not null comment '上传用户id',
#     `disabled`    tinyint unsigned not null comment '是否停用',
#     `create_time` datetime         not null comment '创建时间',
#     primary key (id),
#     unique key (hash_str)
# ) comment '音乐资源表'
#     engine = InnoDB
#     default character set utf8mb4;

# drop table if exists `audio_resource`;
# create table `audio_resource`
# (
#     id          bigint unsigned not null comment '雪花id',
#     hash_str    varchar(32)     not null comment '哈希值',
#     media_type  varchar(32)     not null comment '媒体类型',
#     duration    int unsigned    not null comment '时长',
#     file_size   int unsigned    not null comment '文件大小',
#     save_path   varchar(128)    null     comment '存储位置',
#     user_id     bigint unsigned not null comment '上传用户id',
#     create_time datetime        not null comment '创建时间',
#     update_time datetime        not null comment '更新时间',
#     primary key (id),
#     unique key (hash_str)
# ) comment '声音资源表'
#     engine = InnoDB
#     default character set utf8mb4;
