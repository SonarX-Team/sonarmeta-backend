package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户信息
 * @TableName t_user
 */
@TableName(value ="t_user")
@Data
public class UserDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户性别
     */
    private Integer gender;

    /**
     * 用户描述
     */
    private String description;

    /**
     * 出生日期
     */
    private Date birthDate;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * 推特号
     */
    private String twitter;

    /**
     * 钱包账户 
     */
    private String address;

    /**
     * 钱包余额
     */
    private Long balance;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}