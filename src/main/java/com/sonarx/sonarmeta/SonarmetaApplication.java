package com.sonarx.sonarmeta;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages="com.sonarx.sonarmeta.web.controller")
@MapperScan("com.sonarx.sonarmeta.mapper")
@EnableSwagger2
public class SonarmetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonarmetaApplication.class, args);
    }

}
