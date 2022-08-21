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
     * 体验费用
     */
    private Long experiencePrice;

    /**
     * 对应NFT TokenId
     */
    private Long nftTokenId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public SceneDO(){}

    public SceneDO(SceneDO sceneDO) {
        this.id = sceneDO.id;
        this.status = sceneDO.status;
        this.title = sceneDO.title;
        this.description = sceneDO.description;
        this.cover = sceneDO.cover;
        this.tags = sceneDO.tags;
        this.category = sceneDO.category;
        this.createTime = sceneDO.createTime;
        this.purchasePrice = sceneDO.purchasePrice;
        this.grantPrice = sceneDO.grantPrice;
        this.experiencePrice = sceneDO.experiencePrice;
        this.nftTokenId = sceneDO.nftTokenId;
    }
}
