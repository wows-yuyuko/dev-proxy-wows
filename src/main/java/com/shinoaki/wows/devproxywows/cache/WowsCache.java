package com.shinoaki.wows.devproxywows.cache;

import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.devproxywows.config.WowsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 缓存服务
 *
 * @author Xun
 * @date 2023/5/31 10:11 星期三
 */
@Component
public class WowsCache {
    private static final Logger log = LoggerFactory.getLogger(WowsCache.class);
    private final WowsConfig wowsConfig;

    public WowsCache(WowsConfig wowsConfig) {
        this.wowsConfig = wowsConfig;
    }

    public static boolean checkAccountId(String accountId) {
        return accountId != null && !accountId.isBlank();
    }

    public void accountInfo(long accountId, JsonNode node) {
        writer(accountId, "accountInfo", node);
    }

    public JsonNode accountInfo(long accountId) {
        return readCache(accountId, "accountInfo");
    }

    public void clansAccountInfo(long accountId, JsonNode node) {
        writer(accountId, "clansAccountInfo", node);
    }

    public JsonNode clansAccountInfo(long accountId) {
        return readCache(accountId, "clansAccountInfo");
    }

    public void shipsStats(long accountId, JsonNode node) {
        writer(accountId, "shipsStats", node);
    }

    public JsonNode shipsStats(long accountId) {
        return readCache(accountId, "shipsStats");
    }


    @Scheduled(cron = "0 0/20 * * * ?")
    public void cacheClear() {
        log.info("清理文件缓存!");
        File file = new File(this.wowsConfig.getCachePath());
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                Instant day = LocalDateTime.now().minusMinutes(-20).toInstant(ZoneOffset.ofHours(+8));
                for (var info : listFiles) {
                    try {
                        //检测文件夹创建时间
                        BasicFileAttributes attributes = Files.readAttributes(info.toPath(), BasicFileAttributes.class);
                        if (attributes.creationTime().toInstant().isAfter(day)) {
                            //删除文件夹
                            Files.delete(info.toPath());
                            log.info("删除缓存 {}", info.getPath());
                        }
                    } catch (IOException e) {
                        log.error("{} 缓存检测异常", info.getPath(), e);
                    }
                }
            }
        }
    }

    private JsonNode readCache(long accountId, String module) {
        File file = new File(path(accountId, module));
        if (file.exists()) {
            JsonUtils utils = new JsonUtils();
            try (FileInputStream out = new FileInputStream(file)) {
                return utils.parse(new String(out.readAllBytes(), StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.error("{}-{} 读缓存异常", module, accountId, e);
            }
        }
        return null;
    }

    private void writer(long accountId, String module, JsonNode node) {
        String path = path(accountId, module);
        JsonUtils utils = new JsonUtils();
        File file = new File(path);
        if (!file.exists()){
            new File(path.substring(0,path.lastIndexOf(File.separator))).mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(utils.toJson(node).getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            log.error("{}-{} 写缓存异常", module, accountId, e);
        }
    }

    private String path(long accountId, String module) {
        String path;
        if (this.wowsConfig.getCachePath().startsWith("/") || this.wowsConfig.getCachePath().startsWith(File.separator)) {
            path = this.wowsConfig.getCachePath();
        } else {
            path = System.getProperty("user.dir") + File.separator + this.wowsConfig.getCachePath();
        }
        if (!this.wowsConfig.getCachePath().endsWith("/") || !this.wowsConfig.getCachePath().endsWith(File.separator)) {
            path = path + File.separator;
        }
        return path + accountId + File.separator + module + ".json";
    }
}
