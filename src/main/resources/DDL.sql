-- database scripts for create tables
drop database sonarmeta;
create database sonarmeta;
use sonarmeta;

create table `sonarmeta`.`t_user`
(
    `username`    VARCHAR(255) COMMENT '用户姓名',
    `telephone`   VARCHAR(255) COMMENT '用户手机号',
    `email`       VARCHAR(255) COMMENT '用户邮箱',
    `avatar`      VARCHAR(255)          DEFAULT 'https://ik.imagekit.io/bayc/assets/ape1.png' COMMENT '用户头像',
    `gender`      INT                   DEFAULT 0 COMMENT '用户性别',
    `description` TEXT COMMENT '用户描述',
    `birth_date`  TIMESTAMP              DEFAULT CURRENT_TIMESTAMP COMMENT '出生日期',
    `wechat`      TEXT COMMENT '微信号',
    `twitter`     TEXT COMMENT '推特号',
    `address`     VARCHAR(255) NOT NULL COMMENT '钱包账户',
    `balance`     BIGINT       NOT NULL DEFAULT 0 COMMENT '钱包余额',
    PRIMARY KEY (`address`)
) COMMENT ='用户信息';

create table `sonarmeta`.`t_user_model_ownership_relation`
(
    `id`             BIGINT    NOT NULL AUTO_INCREMENT,
    `address`        VARCHAR(255)    NOT NULL COMMENT '用户地址',
    `model_id`       BIGINT    NOT NULL COMMENT '模型ID',
    `ownership_type` INT       NOT NULL COMMENT '拥有权类型',
    `create_time`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT ='用户和模型对应关系信息';

create table `sonarmeta`.`t_user_scene_ownership_relation`
(
    `id`             BIGINT    NOT NULL AUTO_INCREMENT,
    `address`        VARCHAR(255)    NOT NULL COMMENT '用户地址',
    `scene_id`       BIGINT    NOT NULL COMMENT '场景ID',
    `ownership_type` INT       NOT NULL COMMENT '拥有权类型',
    `create_time`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT ='用户和场景对应关系信息';

create table `sonarmeta`.`t_model`
(
    `id`               BIGINT        NOT NULL AUTO_INCREMENT,
    `path`             TEXT          NOT NULL COMMENT '素材源',
    `attached`         TEXT          NULL COMMENT '',
    `path_folder_list` TEXT          NOT NULL COMMENT '',
    `status`           INT                    DEFAULT 1 COMMENT '模型状态',
    `title`            VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '模型名称',
    `description`      TEXT COMMENT '模型描述',
    `cover`            TEXT          NULL COMMENT '模型封面',
    `tags`             VARCHAR(1024) NOT NULL COMMENT '模型标签',
    `category`         VARCHAR(2)    NOT NULL COMMENT '模型分类',
    `create_time`      TIMESTAMP              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `grant_flag`       INT                   DEFAULT 0 COMMENT '是否可借用',
    `token_flag`        INT                   DEFAULT 0 COMMENT '是否可作为NFT出售',
    `grant_price`      BIGINT        NOT NULL DEFAULT 0 COMMENT '借用费用',
    `token_price`      BIGINT        NOT NULL DEFAULT 0 COMMENT '模型NFT费用',
    `nft_token_id`     BIGINT        NOT NULL COMMENT '对应NFT TokenId',
    PRIMARY KEY (`id`)
) COMMENT ='模型信息';

create table `sonarmeta`.`t_scene`
(
    `id`               BIGINT        NOT NULL AUTO_INCREMENT,
    `status`           INT                    DEFAULT 1 COMMENT '场景状态',
    `title`            VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '场景名称',
    `description`      TEXT COMMENT '场景描述',
    `cover`            TEXT          NULL COMMENT '场景封面',
    `tags`             VARCHAR(1024) NOT NULL COMMENT '场景标签',
    `category`         VARCHAR(2)    NOT NULL COMMENT '场景分类',
    `create_time`      TIMESTAMP              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `dive_flag`       INT                   DEFAULT 0 COMMENT '是否可体验',
    `token_flag`        INT                   DEFAULT 0 COMMENT '是否可作为NFT出售',
    `dive_price` BIGINT        NOT NULL DEFAULT 0 COMMENT '体验费用',
    `token_price`   BIGINT        NOT NULL DEFAULT 0 COMMENT '场景NFT费用',
    `nft_token_id`     BIGINT        NOT NULL COMMENT '对应NFT TokenId',
    PRIMARY KEY (`id`)
) COMMENT ='场景信息';

create table `sonarmeta`.`t_scene_model_relation`
(
    `id`       BIGINT NOT NULL AUTO_INCREMENT,
    `scene_id` BIGINT NOT NULL COMMENT '场景ID',
    `model_id` BIGINT NOT NULL COMMENT '模型ID',
    PRIMARY KEY (`id`)
) COMMENT ='场景和模型对应关系信息';

create table `sonarmeta`.`t_model_basic_settings`
(
    id                        bigint auto_increment,
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
    near                      varchar(255) not null,
    PRIMARY KEY (`id`)
) COMMENT ='模型基本设置';

create table `sonarmeta`.`t_model_material_settings`
(
    id                   bigint auto_increment,
    model_id             bigint   not null,
    textures             longtext null,
    current_pbr_workflow int      not null,
    parameters           longtext not null,
    PRIMARY KEY (`id`)
) COMMENT ='模型材料设置';

create table `sonarmeta`.`t_model_light_settings`
(
    id                                  bigint auto_increment,
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
    light_two_ground_color              varchar(10)  not null,
    PRIMARY KEY (`id`)
) COMMENT ='模型光线设置';

create table `sonarmeta`.`t_model_postprocessing_settings`
(
    id                          bigint auto_increment,
    model_id                    bigint       not null,
    postprocessing_switch       tinyint(1)   not null,
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
    bloom_radius                varchar(255) not null,
    PRIMARY KEY (`id`)
) COMMENT ='模型后处理设置';



