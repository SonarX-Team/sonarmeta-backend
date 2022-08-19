package com.sonarx.sonarmeta.domain.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 编辑模型的表单
 * @author: liuxuanming
 */
@Data
public class EditModelForm {
    @NotNull(message = "用户不能为空")
    private Long userId;

    @NotNull(message = "模型不能为空")
    private Long id;

    private String path;

    private String attached;

    private String pathFolderList;

    private String title;

    private String description;

    private String cover;

    private String tags;

    private String category;

    private Long purchasePrice;

    private Long grantPrice;

    private Long experiencePrice;

}
