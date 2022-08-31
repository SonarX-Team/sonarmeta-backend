package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("创建场景信息Form")
public class CreateSceneForm {

    @NotBlank(message = "用户不能为空")
    @ApiModelProperty(value = "发起创建请求的用户地址")
    private String userAddress;

    @NotNull(message = "场景状态不能为空")
    private Integer status;

    @NotBlank(message = "场景名称不能为空")
    private String title;

    private String description;

    @NotBlank(message = "封面不能为空")
    private String cover;

    @NotBlank(message = "场景标签不能为空")
    private String tags;

    @NotBlank(message = "场景分类不能为空")
    private String category;

    @NotNull(message = "是否可体验不能为空")
    private Integer diveFlag;

    @NotNull(message = "是否可作为NFT出售不能为空")
    private Integer tokenFlag;

    @NotNull(message = "体验费用不能为空")
    private Double divePrice;

    @NotNull(message = "购买费用不能为空")
    private Double tokenPrice;

    @NotNull(message = "模型列表不能为空")
    private List<Long> modelIdList;
}
