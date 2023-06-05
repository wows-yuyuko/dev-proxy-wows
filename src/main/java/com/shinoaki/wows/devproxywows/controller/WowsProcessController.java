package com.shinoaki.wows.devproxywows.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.codec.HttpSend;
import com.shinoaki.wows.api.codec.http.WowsHttpShipTools;
import com.shinoaki.wows.api.error.HttpStatusException;
import com.shinoaki.wows.api.type.WowsBattlesType;
import com.shinoaki.wows.api.type.WowsServer;
import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.devproxywows.cache.WowsCache;
import com.shinoaki.wows.devproxywows.config.WowsConfig;
import com.shinoaki.wows.devproxywows.model.view.UserInfoVO;
import com.shinoaki.wows.devproxywows.service.WowsProcessService;
import com.shinoaki.wows.devproxywows.utils.PathUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Map;

/**
 * @author Xun
 * @date 2023/6/5 15:53 星期一
 */
@Tag(name = "数据处理")
@RestController()
@RequestMapping(value = "/process/wows/", produces = MediaType.APPLICATION_JSON_VALUE)
public class WowsProcessController {


    private final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    private final WowsCache wowsCache;
    private final WowsConfig wowsConfig;
    private final WowsProcessService wowsProcessService;

    public WowsProcessController(WowsCache wowsCache, WowsConfig wowsConfig, WowsProcessService wowsProcessService) {

        this.wowsCache = wowsCache;
        this.wowsConfig = wowsConfig;
        this.wowsProcessService = wowsProcessService;
    }

    @Operation(summary = "返回请求地址", description = "dev类型下建议使用这个返回的数据请求后去调用解析接口")
    @GetMapping(value = "user/ship/list/uri/{server}/server/{accountId}")
    public Mono<Map<String, String>> shipListUri(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server,
                                                 @PathVariable @Parameter(example = "2022515210", description = "账号ID") long accountId) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            WowsHttpShipTools tools = new WowsHttpShipTools(new JsonUtils(), client, code, accountId);
            return Mono.just(Map.of("dev", tools.developers("907d9c6bfc0d896a2c156e57194a97cf").shipListUri().toString(), "vortex",
                    tools.vortex().shipListUri(WowsBattlesType.PVP).toString()));
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }


    @Operation(summary = "查询用户信息和战舰信息", description = "返回空表示服务器不对,服务器列表:asia,eu,na")
    @GetMapping(value = "user/info/{server}/{accountId}/query/{shipId}")
    public Mono<UserInfoVO> userInfo(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server, @PathVariable @Parameter(example =
            "2022515210", description = "账号ID") long accountId, @PathVariable @Parameter(example = "4276041424", description = "战舰ID") long shipId) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            JsonUtils utils = new JsonUtils();
            boolean status = WowsCache.checkAccountId(String.valueOf(accountId));
            if (status) {
                JsonNode node = this.wowsCache.shipsStats(accountId);
                if (node != null) {
                    return this.wowsProcessService.userInfoDev(accountId, node, shipId);
                }
            }
            WowsHttpShipTools tools = new WowsHttpShipTools(utils, client, code, accountId);
            try {
                JsonNode node = utils.parse(HttpSend.sendGet(client, tools.developers(this.wowsConfig.getKey()).shipListUri()));
                this.wowsCache.shipsStats(accountId, node);
                return this.wowsProcessService.userInfoDev(accountId, node, shipId);
            } catch (IOException | HttpStatusException e) {
                return Mono.error(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Mono.error(e);
            }
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }


    @Operation(summary = "上传解析用户数据", description = "dev建议使用shipList返回的url去请求拿数据")
    @PostMapping(value = "user/info/{server}/upload/{dataType}/data/{battleType}/battle/{accountId}/query/{shipId}", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<UserInfoVO> userData(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server, @PathVariable @Parameter(example =
            "vortex", description = "数据类型[vortex,dev]") String dataType, @PathVariable @Parameter(example = "PVP", description = "数战斗数据类型[PVP,PVP_SOLO," +
            "RANK_SOLO]全大写等") String battleType, @PathVariable @Parameter(example = "2022515210", description = "账号ID") long accountId,
                                     @PathVariable @Parameter(example = "4276041424", description = "战舰ID") long shipId,
                                     @RequestPart(value = "files") FilePart file) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            JsonUtils utils = new JsonUtils();
            try {
                var node = utils.parse(PathUtils.temp(file));
                if ("dev".equalsIgnoreCase(dataType)) {
                    return this.wowsProcessService.userInfoDev(accountId, node, shipId);
                } else {
                    return this.wowsProcessService.userInfoVortex(accountId, WowsBattlesType.findCode(battleType), node, shipId);
                }
            } catch (IOException e) {
                return Mono.error(e);
            }
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }
}
