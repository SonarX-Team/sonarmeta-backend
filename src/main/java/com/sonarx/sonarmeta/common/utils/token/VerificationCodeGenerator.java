package com.sonarx.sonarmeta.common.utils.token;

import java.util.Random;

/**
 * @author liuxuanming
 * @description: 验证码生成
 */
public class VerificationCodeGenerator {

    private final static String charSet = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final static Integer codeLength = 4;

    public static String generate() {
        Random random = new Random();
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < codeLength; i++) {
            int rand = random.nextInt(charSet.length());
            code.append(charSet.charAt(rand));
        }
        return code.toString();
    }

}
