package com.sonarx.sonarmeta.domain.query;

import com.sonarx.sonarmeta.domain.common.PageParam;
import lombok.Data;

/**
 * @description: 搜索作品表单
 * @author: liuxuanming
 */
@Data
public class SearchWorksListQuery extends PageParam {

    private String title;

    private String tags;

}
