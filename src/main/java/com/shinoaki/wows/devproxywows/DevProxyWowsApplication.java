package com.shinoaki.wows.devproxywows;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@OpenAPIDefinition(info = @Info(title = "yuyuko战舰世界API平台接口处理与反向代理", version = "0.0.1"
        , description = "开发交流群:967546463"))
@EnableAsync
@SpringBootApplication
public class DevProxyWowsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevProxyWowsApplication.class, args);
    }

}
