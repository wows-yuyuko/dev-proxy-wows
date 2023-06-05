package com.shinoaki.wows.devproxywows.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Xun
 * @date 2023/5/27 0:25 星期六
 */
@Configuration
public class WowsConfig {
    /**
     * 毛子开发者平台的key
     */
    @Value("${wows.key}")
    private String key;

    @Value("${wows.public-key}")
    private String publicKey;

    /**
     * 缓存路径,为空则使用默认的项目根路径的cache
     */
    @Value("${wows.cache.path}")
    private String cachePath;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getCachePath() {
        return cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }
}
