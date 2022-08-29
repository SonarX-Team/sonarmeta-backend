package com.sonarx.sonarmeta.domain.form;

import com.sonarx.sonarmeta.domain.enums.ConsumeTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jnr.ffi.annotations.In;
import lombok.Data;
import org.web3j.abi.datatypes.primitive.Int;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description:用户消费行为表单
 * @author: liuxuanming
 */
@Data
@ApiModel("用户消费行为Form")
public class ConsumeActionForm {

    @ApiModelProperty("用户ID")
    @NotBlank(message = "用户不能为空")
    private String userAddress;

    @ApiModelProperty("模型或场景ID")
    @NotNull(message = "消费对象不能为空")
    private Long id;

    @ApiModelProperty("消费类型")
    @NotNull(message = "类消费型不能为空")
    private Integer type;

}
