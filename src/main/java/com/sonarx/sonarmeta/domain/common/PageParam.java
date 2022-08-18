package com.sonarx.sonarmeta.domain.common;

import com.sonarx.sonarmeta.common.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: When RPC involves a list of data, the request should contain page and pageSize parameters.
 * @author: liuxuanming
 */
@Data
@NoArgsConstructor
public class PageParam implements Serializable {

    private Integer page;

    private Integer pageSize;

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
