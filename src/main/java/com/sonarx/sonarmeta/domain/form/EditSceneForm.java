package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("编辑场景信息Form")
public class EditSceneForm {

    @NotNull(message = "用户不能为空")
    @ApiModelProperty(value = "发起修改请求的用户id")
    private Long userId;

    @NotNull(message = "场景不能为空")
    @ApiModelProperty(value = "场景id")
    private Long id;

    private String title;

    private String description;

    private String cover;

    private String tags;

    private String category;

    private Long purchasePrice;

    private Long grantPrice;

    private Long experiencePrice;
}
