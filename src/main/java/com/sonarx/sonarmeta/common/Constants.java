package com.sonarx.sonarmeta.common;

import org.springframework.stereotype.Component;

/**
 * @Description: 常量
 * @author: liuxuanming
 */
@Component
public class Constants {

    public static final String APP_NAME = "sonarmeta";

    // API版本号
    public static final String CURRENT_API_VERSION = "/v2";

    // API前缀
    public static final String API_PREFIX = "/api" + CURRENT_API_VERSION;

    // 默认连接符
    public static final String DEFAULT_DELIMITER = ";";

    public static final String DEFAULT_CONNECTOR = "-";

    // 分页查询系统最大页大小
    public static final Integer MAX_PAGE_SIZE = 50;

    // 分页查询系统默认页大小
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    // 分页查询系统默认页
    public static final Integer DEFAULT_PAGE = 1;

    // 请求头中带有Token的键
    public static final String TOKEN_HEADER = "Authorization";

}
