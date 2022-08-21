package com.sonarx.sonarmeta.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("编辑用户信息Form")
public class UpdateUserForm {

    @NotNull(message = "用户不能为空")
    @ApiModelProperty(value = "发起修改请求的用户id")
    private Long id;

    private String email;

    private String avatar;

    private Integer gender;

    private String description;

    private Date birthDate;

    private String wechat;

    private String twitter;

    private Long balance;

}
