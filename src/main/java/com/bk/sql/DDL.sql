create table t_comm_city
(
    id             int auto_increment                    NOT NULL COMMENT '主键' primary key,
    city_id        int         DEFAULT NULL COMMENT '省市区ID',
    city_name      varchar(40) DEFAULT NULL COMMENT '省市区名称',
    parent_city_id int         DEFAULT NULL COMMENT '上级ID',
    city_level     tinyint(2)  DEFAULT NULL COMMENT '级别:0-中国;1-省份;2-市;3-区、县',
    created_by     varchar(50) default 'SYSTEM'          not null,
    created_date   datetime    default CURRENT_TIMESTAMP not null,
    updated_by     varchar(50) default 'SYSTEM'          not null,
    updated_date   datetime    default CURRENT_TIMESTAMP not null
)
    comment '公用城市代码表' collate = utf8mb4_bin;
