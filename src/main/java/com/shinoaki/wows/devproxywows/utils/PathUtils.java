package com.shinoaki.wows.devproxywows.utils;

import com.shinoaki.wows.api.codec.HttpCodec;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

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

    public static String temp(FilePart part) throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + UUID.randomUUID() + ".json");
        part.transferTo(file).subscribe();
        try (FileInputStream in = new FileInputStream(file)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }
}
