alter table ${}
    add column id          bigint unsigned auto_increment not null comment '雪花id' first,
    add column user_id     int unsigned                   not null comment '用户id',
    add column create_by   int unsigned default 0         not null comment '创建人id',
    add column update_by   int unsigned default 0         not null comment '更新人id',
    add column create_time datetime     default now()     not null comment '创建时间',
    add column update_time datetime     default now()     not null comment '修改时间' on update now(),
    add column remark      varchar(64)  default ''        not null comment '备注',
    add column version     int unsigned default 0         not null comment '乐观锁',
    add column delete_flag bit(1)       default 0         null comment '逻辑删除',
    add primary key pk_id (id),
    add key idx_user (user_id);
