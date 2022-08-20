package com.sonarx.sonarmeta.domain.common;

import com.sonarx.sonarmeta.common.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: When RPC involves a list of data, the request should contain page and pageSize parameters.
 * @author: liuxuanming
 */
@Data
@NoArgsConstructor
@ApiModel(description = "分页参数")
public class PageParam implements Serializable {

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "页容量")
    private Integer pageSize;

    @ApiModelProperty(value = "偏移量，不一定需要传输", required = false)
    private Integer offset;

    public void setOffset() {
        if(getPage() != null && getPageSize() != null) {
            setOffset((getPage() - 1) * getPageSize());
        }
        else {
            setOffset(0);
        }
    }

    public static void verify(PageParam pageParam) {
        if (pageParam.getPage() == null || pageParam.getPage() <= 0) {
            pageParam.setPage(Constants.DEFAULT_PAGE);
        }
        if (pageParam.getPageSize() == null || pageParam.getPageSize() <= 0) {
            pageParam.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        }
        if (pageParam.getPageSize() > Constants.MAX_PAGE_SIZE) {
            pageParam.setPageSize(Constants.MAX_PAGE_SIZE);
        }
        if (pageParam.getOffset() == null) {
            pageParam.setOffset();
        }
    }
}
