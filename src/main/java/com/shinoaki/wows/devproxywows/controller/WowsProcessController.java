package com.shinoaki.wows.devproxywows.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.codec.HttpSend;
import com.shinoaki.wows.api.codec.http.WowsHttpShipTools;
import com.shinoaki.wows.api.error.HttpStatusException;
import com.shinoaki.wows.api.type.WowsServer;
import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.devproxywows.cache.WowsCache;
import com.shinoaki.wows.devproxywows.config.WowsConfig;
import com.shinoaki.wows.devproxywows.model.view.UserInfoVO;
import com.shinoaki.wows.devproxywows.service.WowsProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.http.HttpClient;

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


    @Operation(summary = "查询用户信息和战舰信息", description = "返回空表示服务器不对,服务器列表:asia,eu,na")
    @GetMapping(value = "user/info/{server}/{accountId}/query/{shipId}")
    public Mono<UserInfoVO> userInfo(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server,
                                     @PathVariable @Parameter(example = "2022515210", description = "账号ID") long accountId,
                                     @PathVariable @Parameter(example = "4276041424", description = "战舰ID") long shipId) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            JsonUtils utils = new JsonUtils();
            boolean status = WowsCache.checkAccountId(String.valueOf(accountId));
            if (status) {
                JsonNode node = this.wowsCache.shipsStats(accountId);
                if (node != null) {
                    return this.wowsProcessService.userInfo(accountId, node, shipId);
                }
            }
            WowsHttpShipTools tools = new WowsHttpShipTools(utils, client, code, accountId);
            try {
                JsonNode node = utils.parse(HttpSend.sendGet(client, tools.developers(this.wowsConfig.getKey()).shipListUri()));
                this.wowsCache.shipsStats(accountId, node);
                return this.wowsProcessService.userInfo(accountId, node, shipId);
            } catch (IOException | HttpStatusException e) {
                return Mono.error(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Mono.error(e);
            }
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }
}
