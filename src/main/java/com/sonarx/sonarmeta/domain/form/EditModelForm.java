package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 编辑模型的表单
 * @author: liuxuanming
 */
@Data
@ApiModel("编辑模型信息Form")
public class EditModelForm {

    @NotNull(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户地址")
    private String userAddress;

    @NotNull(message = "模型不能为空")
    @ApiModelProperty(value = "模型id")
    private Long id;

    @NotBlank(message = "模型路径不能为空")
    private String path;

    private String attached;

    private String pathFolderList;

    private String title;

    private String description;

    private String cover;

    private String tags;

    private String category;

    private Integer grantFlag;

    private Integer tokenFlag;

    private Long grantPrice;

    private Long tokenPrice;

}
