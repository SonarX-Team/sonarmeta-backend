package com.sonarx.sonarmeta.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @description: 创建模型的表单
 * @author: liuxuanming
 */
@Data
public class CreateModelForm {

    @NotNull(message = "用户不能为空")
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
