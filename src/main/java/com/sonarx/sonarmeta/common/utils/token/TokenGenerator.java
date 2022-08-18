package com.sonarx.sonarmeta.common.utils.token;

import org.springframework.util.DigestUtils;

/**
 * @Description: Token generator.
 * @author: liuxuanming
 */
public class TokenGenerator {

    public static String generate(String... strings) {
        long timestamp = System.currentTimeMillis();
        String tokenMeta = "";
        for(String s : strings) {
            tokenMeta = tokenMeta + s;
        }
        tokenMeta = tokenMeta + timestamp;
        return DigestUtils.md5DigestAsHex(tokenMeta.getBytes());
    }

}
