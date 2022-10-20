-- database scripts for create tables
drop database sonarmeta;
create database sonarmeta;
use sonarmeta;

create table if not exists t_model
(
    id               bigint auto_increment
        primary key,
    path             text                                   not null comment '素材源',
    attached         text                                   null,
    path_folder_list text                                   not null,
    status           int          default 1                 null comment '模型状态',
    title            varchar(255) default ''                not null comment '模型名称',
    description      text                                   null comment '模型描述',
    cover            text                                   null comment '模型封面',
    tags             varchar(1024)                          not null comment '模型标签',
    category         varchar(2)                             not null comment '模型分类',
    create_time      timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    grant_flag       int          default 0                 null comment '是否可借用',
    token_flag       int          default 0                 null comment '是否可作为NFT出售',
    grant_price      bigint       default 0                 not null comment '借用费用',
    token_price      bigint       default 0                 not null comment '模型NFT费用',
    nft_token_id     bigint                                 not null comment '对应NFT TokenId'
)
    comment '模型信息';

create table if not exists t_model_basic_settings
(
    id                        bigint auto_increment
        primary key,
    model_id                  bigint       not null,
    model_rotation_x          varchar(255) not null,
    model_rotation_y          varchar(255) not null,
    model_rotation_z          varchar(255) not null,
    camera_position_x         varchar(255) not null,
    camera_position_y         varchar(255) not null,
    camera_position_z         varchar(255) not null,
    fov                       varchar(255) not null,
    background_switch         tinyint(1)   not null,
    background_choice         int          not null,
    background_color          varchar(10)  null,
    background_image          longtext     null,
    background_env            longtext     null,
    background_env_brightness varchar(255) not null,
    shading                   int          not null,
    near                      varchar(255) not null
)
    comment '模型基本设置';

create table if not exists t_model_light_settings
(
    id                                  bigint auto_increment
        primary key,
    model_id                            bigint       not null,
    light_switch                        tinyint(1)   not null,
    light_one_type                      int          not null,
    light_one_color                     varchar(10)  not null,
    light_one_position_x                varchar(255) not null,
    light_one_position_y                varchar(255) not null,
    light_one_position_z                varchar(255) not null,
    light_one_intensity                 varchar(255) not null,
    light_one_cast_shadow               tinyint(1)   not null,
    light_one_shadow_bias               varchar(255) not null,
    light_one_attached_to_camera        tinyint(1)   not null,
    light_one_decay                     varchar(255) not null,
    light_one_angle                     varchar(255) not null,
    light_one_penumbra                  varchar(255) not null,
    light_two_type                      int          not null,
    light_two_color                     varchar(10)  not null,
    light_two_position_x                varchar(255) not null,
    light_two_position_y                varchar(255) not null,
    light_two_position_z                varchar(255) not null,
    light_two_intensity                 varchar(255) not null,
    light_two_cast_shadow               tinyint(1)   not null,
    light_two_shadow_bias               varchar(255) not null,
    light_two_attached_to_camera        tinyint(1)   not null,
    light_two_decay                     varchar(255) not null,
    light_two_angle                     varchar(255) not null,
    light_two_penumbra                  varchar(255) not null,
    light_three_type                    int          not null,
    light_three_color                   varchar(10)  not null,
    light_three_position_x              varchar(255) not null,
    light_three_position_y              varchar(255) not null,
    light_three_position_z              varchar(255) not null,
    light_three_intensity               varchar(255) not null,
    light_three_cast_shadow             tinyint(1)   not null,
    light_three_shadow_bias             varchar(255) not null,
    light_three_attached_to_camera      tinyint(1)   not null,
    light_three_decay                   varchar(255) not null,
    light_three_angle                   varchar(255) not null,
    light_three_penumbra                varchar(255) not null,
    env_switch                          tinyint(1)   not null,
    env_texture                         longtext     null,
    env_orientation                     varchar(255) not null,
    env_brightness                      varchar(255) not null,
    ambient_directional_light_bias      varchar(255) not null,
    ambient_directional_light_intensity varchar(255) not null,
    ambient_directional_light_switch    tinyint(1)   not null,
    light_one_ground_color              varchar(10)  not null,
    light_three_ground_color            varchar(10)  not null,
    light_two_ground_color              varchar(10)  not null
)
    comment '模型光线设置';

create table if not exists t_model_material_settings
(
    id                   bigint auto_increment
        primary key,
    model_id             bigint   not null,
    textures             longtext null,
    current_pbr_workflow int      not null,
    parameters           longtext not null
)
    comment '模型材料设置';

create table if not exists t_model_postprocessing_settings
(
    id                          bigint auto_increment
        primary key,
    model_id                    bigint       not null,
    switch                      tinyint(1)   not null,
    ssao_switch                 tinyint(1)   not null,
    ssao_radius                 varchar(255) not null,
    ssao_intensity              varchar(255) not null,
    ssao_bias                   varchar(255) not null,
    chromatic_aberration_switch tinyint(1)   not null,
    chromatic_aberration_offset varchar(255) not null,
    vignette_switch             tinyint(1)   not null,
    vignette_offset             varchar(255) not null,
    vignette_darkness           varchar(255) not null,
    bloom_switch                tinyint(1)   not null,
    bloom_intensity             varchar(255) not null,
    bloom_luminance_threshold   varchar(255) not null,
    bloom_radius                varchar(255) not null
)
    comment '模型后处理设置';

create table if not exists t_scene
(
    id           bigint auto_increment
        primary key,
    status       int          default 1                 null comment '场景状态',
    title        varchar(255) default ''                not null comment '场景名称',
    description  text                                   null comment '场景描述',
    cover        text                                   null comment '场景封面',
    tags         varchar(1024)                          not null comment '场景标签',
    category     varchar(2)                             not null comment '场景分类',
    create_time  timestamp    default CURRENT_TIMESTAMP not null comment '创建时间',
    dive_flag    int          default 0                 null comment '是否可体验',
    token_flag   int          default 0                 null comment '是否可作为NFT出售',
    dive_price   bigint       default 0                 not null comment '体验费用',
    token_price  bigint       default 0                 not null comment '场景NFT费用',
    nft_token_id bigint                                 not null comment '对应NFT TokenId'
)
    comment '场景信息';

create table if not exists t_scene_model_relation
(
    id       bigint auto_increment
        primary key,
    scene_id bigint not null comment '场景ID',
    model_id bigint not null comment '模型ID'
)
    comment '场景和模型对应关系信息';

create table if not exists t_user
(
    username    varchar(255)                                                       null comment '用户姓名',
    telephone   varchar(255)                                                       null comment '用户手机号',
    email       varchar(255)                                                       null comment '用户邮箱',
    avatar      varchar(255) default 'https://ik.imagekit.io/bayc/assets/ape1.png' null comment '用户头像',
    gender      int          default 0                                             null comment '用户性别',
    description text                                                               null comment '用户描述',
    birth_date  timestamp    default CURRENT_TIMESTAMP                             not null comment '出生日期',
    wechat      text                                                               null comment '微信号',
    twitter     text                                                               null comment '推特号',
    address     varchar(255)                                                       not null comment '钱包账户'
        primary key,
    balance     bigint       default 0                                             not null comment '钱包余额'
)
    comment '用户信息';

create table if not exists t_user_model_ownership_relation
(
    id             bigint auto_increment
        primary key,
    address        varchar(255)                        not null comment '用户地址',
    model_id       bigint                              not null comment '模型ID',
    ownership_type int                                 not null comment '拥有权类型',
    create_time    timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '用户和模型对应关系信息';

create table if not exists t_user_scene_ownership_relation
(
    id             bigint auto_increment
        primary key,
    address        varchar(255)                        not null comment '用户地址',
    scene_id       bigint                              not null comment '场景ID',
    ownership_type int                                 not null comment '拥有权类型',
    create_time    timestamp default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '用户和场景对应关系信息';



