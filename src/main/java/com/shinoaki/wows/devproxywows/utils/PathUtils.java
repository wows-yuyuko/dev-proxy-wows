package com.shinoaki.wows.devproxywows.utils;

import com.shinoaki.wows.api.codec.HttpCodec;
import org.springframework.util.MultiValueMap;

/**
 * @author Xun
 * @date 2023/5/26 21:24 星期五
 */
public class PathUtils {
    private PathUtils() {

    }


    public static String httpMapByGet(String key, MultiValueMap<String, String> queryParams) {
        StringBuilder builder = new StringBuilder("?application_id=" + key + "&");
        queryParams.toSingleValueMap();
        queryParams.toSingleValueMap().forEach((k, v) -> {
            if (!"application_id".equalsIgnoreCase(k)) {
                builder.append(k).append("=").append(HttpCodec.encodeURIComponent(v)).append("&");
            }
        });
        return builder.deleteCharAt(builder.length() - 1).toString();
    }
}
