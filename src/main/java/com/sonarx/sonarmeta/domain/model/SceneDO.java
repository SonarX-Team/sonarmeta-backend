package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 场景信息
 * @TableName t_scene
 */
@TableName(value ="t_scene")
@Data
public class SceneDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场景状态
     */
    private Integer status;

    /**
     * 场景名称
     */
    private String title;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 场景封面
     */
    private String cover;

    /**
     * 场景标签
     */
    private String tags;

    /**
     * 场景分类
     */
    private String category;

    /**
     * 场景类型
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 购买费用
     */
    private Long purchasePrice;

    /**
     * 借用费用
     */
    private Long grantPrice;

    /**
     * 观看费用
     */
    private Long watchPrice;

    /**
     * 对应NFT TokenId
     */
    private Long nftTokenId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}