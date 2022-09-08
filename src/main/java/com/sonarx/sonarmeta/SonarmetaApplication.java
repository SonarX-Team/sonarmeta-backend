package com.sonarx.sonarmeta;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {"com.sonarx.sonarmeta.web", "com.sonarx.sonarmeta.web.controller", "com.sonarx.sonarmeta.service.impl"})
@MapperScan("com.sonarx.sonarmeta.mapper")
@EnableSwagger2
@EnableAsync
public class SonarmetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonarmetaApplication.class, args);
    }

}
