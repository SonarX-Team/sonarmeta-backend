package com.sonarx.sonarmeta.domain.view;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("模型视图")
public class ModelView {

    private Long id;

    private String path;

    private String attached;

    private String pathFolderList;

    private Integer status;

    private String title;

    private String description;

    private String cover;

    private String tags;

    private String category;

    private Date createTime;

    private Integer grantFlag;

    private Integer tokenFlag;

    private Double grantPrice;

    private Double tokenPrice;

    private Long nftTokenId;

    private String CreatorAvatar;

    private String CreatorUsername;

    private String CreatorAddress;

    private String OwnerAvatar;

    private String OwnerUsername;

    private String OwnerAddress;
}
