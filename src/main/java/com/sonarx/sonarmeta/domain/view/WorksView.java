package com.sonarx.sonarmeta.domain.view;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * @description: 作品视图
 * @author: liuxuanming
 */
@Data
@ApiModel("作品视图")
public class WorksView {

    private Long id;

    private Integer worksType;

    private String title;

    private String cover;

    private String avatar;

    private String username;

    private Date createTime;

    private Double price;

    private String userAddress;

}
