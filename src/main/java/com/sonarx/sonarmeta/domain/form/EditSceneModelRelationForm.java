package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("编辑场景与模型关系Form")
public class EditSceneModelRelationForm {

    @NotBlank(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户地址")
    private String userAddress;

    @NotNull(message = "场景不能为空")
    private Long sceneId;

    @NotNull(message = "模型不能为空")
    private List<Long> modelIdList;

}
