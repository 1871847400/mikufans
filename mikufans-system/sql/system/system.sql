drop table if exists `sys_user`;
create table `sys_user`
(
    username  varchar(32)                 not null comment '用户名',
    password  varchar(255)                not null comment '密码',
    avatar_id bigint unsigned  default 0  not null comment '头像id',
    disabled  tinyint unsigned default 0  not null comment '是否禁用',
    role_ids  varchar(255)     default '' not null comment '角色id列表'
) comment '系统用户表';
create unique index uk_user_name on sys_user (username, delete_flag);

drop table if exists `sys_role`;
create table `sys_role`
(
    role_key  varchar(32)             not null comment '角色关键字',
    role_name varchar(32)             not null comment '角色名称',
    perm_ids  varchar(512) default '' not null comment '权限id列表'
) comment '系统角色表';
create unique index uk_role_key on sys_role (role_key, delete_flag);

drop table if exists `sys_perm`;
create table `sys_perm`
(
    pid       int unsigned default 0 not null comment '父权限id',
    perm_key  varchar(64)            not null comment '权限关键字',
    perm_name varchar(64)            not null comment '权限名称'
) comment '系统权限表';
create unique index uk_perm_key on sys_perm (perm_key, delete_flag);

drop table if exists `sys_param`;
create table `sys_param`
(
    param_key   varchar(64)  not null comment '参数键',
    param_value varchar(256) not null comment '参数值'
) comment '系统参数配置表';
create unique index uk_param_key on sys_param (param_key, delete_flag);

drop table if exists `sys_carousel`;
create table `sys_carousel`
(
    position     tinyint unsigned               not null comment '播放位置',
    channel_id   int unsigned     default 0     not null comment '频道id',
    banner_id    bigint unsigned  default 0     not null comment '封面id',
    thumbnail_id bigint unsigned  default 0     not null comment '缩略图id',
    title        varchar(32)                    not null comment '标题',
    subtitle     varchar(32)      default ''    not null comment '副标题',
    url          varchar(200)     default ''    not null comment '链接',
    disabled     tinyint unsigned default 0     not null comment '是否禁用',
    video_id     bigint unsigned  default 0     not null comment '视频id',
    start_date   datetime         default now() not null comment '启用日期',
    end_date     datetime                       null comment '停用日期',
    main_color   varchar(10)      default ''    not null comment '主色调',
    sort         int unsigned     default 1     not null comment '排序'
) comment '轮播配置表';

drop table if exists `sys_notice`;
create table `sys_notice`
(
    notice_type int unsigned              not null comment '通知类型',
    title       varchar(64)               not null comment '通知标题',
    template    varchar(2048)             not null comment '通知内容模板',
    def_url     varchar(64) default ''    not null comment '默认链接',
    enable_time datetime    default now() not null comment '启用时间',
    expire_time datetime                  null comment '到期时间'
) comment '系统通知模板表';

drop table if exists `sys_audit`;
create table `sys_audit`
(
    target_id    bigint unsigned         not null comment '目标id',
    audit_type   tinyint unsigned        not null comment '审核类型',
    audit_status tinyint unsigned        not null comment '审核状态',
    audit_reason varchar(128) default '' not null comment '不通过的原因',
    index (target_id)
) comment '业务审核表';

drop table if exists `sys_region`;
create table `sys_region`
(
    region_name  varchar(16) not null comment '地区名称',
    region_code  varchar(32) not null comment '地区代码',
    lang_name    varchar(16) not null comment '语言名称',
    lang_code    varchar(32) not null comment '语言代码',
    lang_en_name varchar(24) not null comment '语言英文名称'
) comment '地区和语言配置表';

drop table if exists `sys_ext_dict`;
create table `sys_ext_dict`
(
    term    varchar(32)      not null comment '词条内容',
    illegal tinyint unsigned not null comment '是否为敏感词'
) comment 'ES扩展词库配置表';
create unique index uk_term on sys_ext_dict (term, delete_flag);

drop table if exists `sys_dict_type`;
create table `sys_dict_type`
(
    dict_name varchar(32)                not null comment '字典名称',
    dict_type varchar(32)                not null comment '字典类型',
    mutable   tinyint unsigned default 1 not null comment '能否增加/删除字典数据',
    unique (dict_type) #外键关联必须单列唯一
) comment '字典类型表';

drop table if exists `sys_dict_data`;
create table `sys_dict_data`
(
    dict_type  varchar(32)                 not null comment '字典类型',
    dict_label varchar(32)                 not null comment '字典标签',
    dict_value varchar(32)                 not null comment '字典键值',
    dict_sort  tinyint unsigned default 1  not null comment '字典排序',
    def_flag   tinyint unsigned default 0  not null comment '是否默认',
    disabled   tinyint unsigned default 0  not null comment '是否禁用',
    tag_type   varchar(32)      default '' not null comment '标签类型',
    icon_name  varchar(32)      default '' not null comment '图标名称',
    icon_color varchar(10)      default '' not null comment '图标颜色',
    foreign key (dict_type) references sys_dict_type (dict_type) on update cascade
) comment '字典数据表';
create unique index uk_type_value on sys_dict_data (dict_type, dict_value, delete_flag);