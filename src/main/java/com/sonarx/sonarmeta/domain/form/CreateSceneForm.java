package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("创建场景信息Form")
public class CreateSceneForm {

    @NotNull(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户id")
    private Long userId;

    @NotBlank(message = "场景状态不能为空")
    private Integer status;

    @NotBlank(message = "场景名称不能为空")
    private String title;


    private String description;


    private String cover;

    @NotBlank(message = "场景标签不能为空")
    private String tags;

    @NotBlank(message = "场景分类不能为空")
    private String category;


    @NotBlank(message = "购买费用不能为空")
    private Long purchasePrice;

    @NotBlank(message = "借用费用不能为空")
    private Long grantPrice;

    @NotBlank(message = "体验费用不能为空")
    private Long experiencePrice;
}
