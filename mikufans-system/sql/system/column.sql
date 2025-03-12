alter table ${}
    add column id          int unsigned auto_increment not null comment '自增id' first,
    add column create_by   int unsigned default 0      not null comment '创建人id',
    add column update_by   int unsigned default 0      not null comment '更新人id',
    add column create_time datetime     default now()  not null comment '创建时间',
    add column update_time datetime     default now()  not null comment '修改时间' on update now(),
    add column remark      varchar(64)  default ''     not null comment '备注',
    add column delete_flag bit(1)       default 0      null comment '逻辑删除',
    add primary key pk_id (id);
