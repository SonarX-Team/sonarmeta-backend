package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("编辑场景信息Form")
public class EditSceneForm {

    @NotBlank(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户地址")
    private String userAddress;

    @NotNull(message = "场景不能为空")
    @ApiModelProperty(value = "场景id")
    private Long id;

    private Integer status;

    private String title;

    private String description;

    private String cover;

    private String tags;

    private String category;

    private Integer diveFlag;

    private Integer tokenFlag;

    private Long divePrice;

    private Long tokenPrice;


}
