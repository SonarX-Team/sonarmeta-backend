package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @description: 创建模型的表单
 * @author: liuxuanming
 */
@Data
@ApiModel("创建模型信息Form")
public class CreateModelForm {

    @NotNull(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户id")
    private Long userId;

    @NotBlank(message = "模型路径不能为空")
    private String path;

    @NotBlank(message = "模型路径不能为空")
    private String attached;

    @NotBlank(message = "模型路径不能为空")
    private String pathFolderList;

    @NotBlank(message = "模型标题不能为空")
    private String title;

    private String description;

    private String cover;

    @NotBlank(message = "模型标签不能为空")
    private String tags;

    @NotBlank(message = "模型分类不能为空")
    private String category;
}
