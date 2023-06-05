package com.shinoaki.wows.devproxywows.controller;

import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.devproxywows.model.valid.UserInfoValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * @author Xun
 * @date 2023/6/4 17:38 星期日
 */
@Tag(name = "数据上传中心")
@RestController
@RequestMapping(value = "/upload/wows/")
public class WowsUploadDataController {
    public static final Logger log = LoggerFactory.getLogger(WowsUploadDataController.class);

    @Operation(summary = "上传对局信息", description = "服务器列表:asia,eu,na")
    @PostMapping(value = "game/player", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String uploadPlayerInfo(@RequestBody UserInfoValid valid) {
        String basePath = System.getProperty("user.dir") + File.separator + "battle" + File.separator + LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String uid = basePath + File.separator + UUID.randomUUID() + ".json";
        File file = new File(uid);
        if (!file.exists()) {
            new File(basePath).mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(new JsonUtils().toJson(valid).getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            log.error("记录对局信息异常!", e);
        }
        return "success";
    }
}
