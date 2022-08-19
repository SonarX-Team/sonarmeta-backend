package com.sonarx.sonarmeta.domain.view;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @description: 作品视图
 * @author: liuxuanming
 */
@Data
public class WorksView {

    private Long id;

    /**
     * 作品类型
     */
    private Integer worksType;

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
     * 状态
     */
    private Integer status;

    /**
     * 名称
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面
     */
    private String cover;

    /**
     * 标签
     */
    private String tags;

    /**
     * 分类
     */
    private String category;

    /**
     * 类型
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
}
