package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 场景信息
 * @TableName t_scene
 */
@TableName(value ="t_scene")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
     * 是否可体验
     */
    private Integer diveFlag;

    /**
     * 是否可作为NFT出售
     */
    private Integer tokenFlag;

    /**
     * 体验费用
     */
    private Long divePrice;

    /**
     * 场景NFT费用
     */
    private Long tokenPrice;

    /**
     * 对应NFT TokenId
     */
    private Long nftTokenId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public SceneDO(SceneDO sceneDO) {
        this.id = sceneDO.id;
        this.status = sceneDO.status;
        this.title = sceneDO.title;
        this.description = sceneDO.description;
        this.cover = sceneDO.cover;
        this.tags = sceneDO.tags;
        this.category = sceneDO.category;
        this.createTime = sceneDO.createTime;
        this.diveFlag = sceneDO.diveFlag;
        this.tokenFlag = sceneDO.tokenFlag;
        this.divePrice = sceneDO.divePrice;
        this.tokenPrice = sceneDO.tokenPrice;
        this.nftTokenId = sceneDO.nftTokenId;
    }
}
