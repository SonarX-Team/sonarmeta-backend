package com.sonarx.sonarmeta.domain.query;

import com.sonarx.sonarmeta.domain.common.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 搜索作品表单
 * @author: liuxuanming
 */
@Data
@ApiModel(description = "搜索作品合集Query")
public class SearchWorksListQuery extends PageParam {

    @ApiModelProperty(value = "title")
    private String title;

    @ApiModelProperty(value = "tags")
    private String tags;

}
