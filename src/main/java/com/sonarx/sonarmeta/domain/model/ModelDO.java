package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 模型信息
 * @TableName t_model
 */
@TableName(value ="t_model")
@Data
public class ModelDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 素材源
     */
    private String path;

    /**
     * 
     */
    private String attached;

    /**
     * 
     */
    private String pathFolderList;

    /**
     * 模型状态
     */
    private Integer status;

    /**
     * 模型名称
     */
    private String title;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 模型封面
     */
    private String cover;

    /**
     * 模型标签
     */
    private String tags;

    /**
     * 模型分类
     */
    private String category;

    /**
     * 模型类型
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