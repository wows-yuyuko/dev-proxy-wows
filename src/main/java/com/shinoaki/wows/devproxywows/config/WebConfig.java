package com.shinoaki.wows.devproxywows.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @author Xun
 * @date 2023/6/5 18:01 星期一
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    public static final int MAX_IN_MEMORY = 10485760;
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY);
    }
}
