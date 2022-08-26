package com.sonarx.sonarmeta.web;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.TimeUnit;

/**
 * @description: config of Web3j
 * @author: liuxuanming
 */
@Configuration
public class Web3jConfig {

    @Value("${web3j.rpc-url}")
    private String rpcUrl;

    private static final Integer timeout = 10 * 1000;

    @Bean
    public Web3j web3j() throws Exception {
        return Web3j.build(
                new HttpService(rpcUrl,
                        new OkHttpClient.Builder()
                                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                                .build(),
                        false)
        );
    }
}
