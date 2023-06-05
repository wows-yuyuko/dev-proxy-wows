package com.shinoaki.wows.devproxywows.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.type.WowsServer;
import com.shinoaki.wows.devproxywows.cache.WowsCache;
import com.shinoaki.wows.devproxywows.config.WebConfig;
import com.shinoaki.wows.devproxywows.config.WowsConfig;
import com.shinoaki.wows.devproxywows.utils.PathUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author Xun
 * @date 2023/5/26 21:48 星期五
 */
@Tag(name = "dev平台接口反代", description = "application_id参数可以忽略,服务器会自动补全(你填了也会被忽略掉)")
@RestController
@RequestMapping(value = "/dev/wows/")
public class WowsDevController {

    private final WowsConfig wowsConfig;
    private final WowsCache wowsCache;
    public static final String ID = "account_id";
    private final WebClient webClient = WebClient.builder().exchangeStrategies(
            //10MB
            ExchangeStrategies.builder().codecs(codec -> codec.defaultCodecs().maxInMemorySize(WebConfig.MAX_IN_MEMORY)).build()
    ).build();

    public WowsDevController(WowsConfig wowsConfig, WowsCache wowsCache) {
        this.wowsConfig = wowsConfig;
        this.wowsCache = wowsCache;
    }


    /**
     * 搜索用户
     *
     * @param request 请求信息
     * @param server  服务器
     * @return 空表示服务器不存在/或不支持
     */
    @Operation(summary = "搜索用户", description = "该接口反代了https://developers.wargaming.net/reference/all/wows/account/list,使用方式请参考该地址的文档")
    @Parameter(name = "search", description = "用户名", required = true, example = "JustOneSummer")
    @GetMapping("search/{server}/")
    public Mono<JsonNode> searchUser(ServerHttpRequest request, @Parameter(example = "asia", required = true) @PathVariable String server) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            return webClient.get()
                    .uri(URI.create(code.api() + "/wows/account/list/" + PathUtils.httpMapByGet(this.wowsConfig.getKey(), request.getQueryParams())))
                    .exchangeToMono(resp -> resp.bodyToMono(JsonNode.class));
        }
        //这里要丢异常出去
        return Mono.empty();
    }

    /**
     * 搜索账号信息
     *
     * @param request 请求信息
     * @param server  服务器
     * @return 空表示服务器不存在/或不支持
     */
    @Operation(summary = "账号信息", description = "该接口反代了https://api.worldofwarships.asia/wows/account/info/,使用方式请参考该地址的文档")
    @Parameter(name = "account_id", description = "用户id", required = true, example = "2022515210")
    @GetMapping("account/info/{server}/")
    public Mono<JsonNode> accountInfo(ServerHttpRequest request, @Parameter(example = "asia", required = true) @PathVariable String server) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            String accountId = request.getQueryParams().getFirst(ID);
            boolean status = WowsCache.checkAccountId(accountId);
            if (status) {
                JsonNode node = this.wowsCache.accountInfo(Long.parseLong(accountId));
                if (node != null) {
                    return Mono.just(node);
                }
            }
            return webClient.get()
                    .uri(URI.create(code.api() + "/wows/account/info/" + PathUtils.httpMapByGet(this.wowsConfig.getKey(), request.getQueryParams())))
                    .exchangeToMono(resp ->
                            resp.bodyToMono(JsonNode.class).flatMap(x -> {
                                if (status) {
                                    this.wowsCache.accountInfo(Long.parseLong(accountId), x);
                                }
                                return Mono.just(x);
                            })
                    );
        }
        //这里要丢异常出去
        return Mono.empty();
    }


    /**
     * 用户公会信息
     *
     * @param request 请求信息
     * @param server  服务器
     * @return 空表示服务器不存在/或不支持
     */
    @Operation(summary = "用户公会信息-默认会追加extra=clan", description = "该接口反代了https://api.worldofwarships.asia/wows/clans/accountinfo/,使用方式请参考该地址的文档")
    @Parameter(name = "account_id", description = "用户id", required = true, example = "2022515210")
    @GetMapping("clans/accountinfo/{server}/")
    public Mono<JsonNode> clansAccountInfo(ServerHttpRequest request, @Parameter(example = "asia", required = true) @PathVariable String server) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            String accountId = request.getQueryParams().getFirst(ID);
            boolean status = WowsCache.checkAccountId(accountId);
            if (status) {
                JsonNode node = this.wowsCache.clansAccountInfo(Long.parseLong(accountId));
                if (node != null) {
                    return Mono.just(node);
                }
            }
            String uri = PathUtils.httpMapByGet(this.wowsConfig.getKey(), request.getQueryParams());
            if (!uri.contains("extra=")) {
                uri = uri + "&extra=clan";
            }
            return webClient.get()
                    .uri(URI.create(code.api() + "/wows/clans/accountinfo/" + uri))
                    .exchangeToMono(resp -> resp.bodyToMono(JsonNode.class).flatMap(x -> {
                        if (status) {
                            this.wowsCache.clansAccountInfo(Long.parseLong(accountId), x);
                        }
                        return Mono.just(x);
                    }));
        }
        //这里要丢异常出去
        return Mono.empty();
    }

    /**
     * 用户船池信息
     *
     * @param request 请求信息
     * @param server  服务器
     * @return 空表示服务器不存在/或不支持
     */
    @Operation(summary = "用户船池信息", description = "该接口反代了https://api.worldofwarships.asia/wows/ships/stats/,使用方式请参考该地址的文档")
    @Parameter(name = "account_id", description = "用户id", required = true, example = "2022515210")
    @GetMapping("ships/stats/{server}/")
    public Mono<JsonNode> shipsStats(ServerHttpRequest request, @Parameter(example = "asia", required = true) @PathVariable String server) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            String accountId = request.getQueryParams().getFirst(ID);
            boolean status = WowsCache.checkAccountId(accountId);
            if (status) {
                JsonNode node = this.wowsCache.shipsStats(Long.parseLong(accountId));
                if (node != null) {
                    return Mono.just(node);
                }
            }
            return webClient.get()
                    .uri(URI.create(code.api() + "/wows/ships/stats/" + PathUtils.httpMapByGet(this.wowsConfig.getKey(), request.getQueryParams())))
                    .exchangeToMono(resp -> resp.bodyToMono(JsonNode.class).flatMap(x -> {
                        if (status) {
                            this.wowsCache.shipsStats(Long.parseLong(accountId), x);
                        }
                        return Mono.just(x);
                    }));
        }
        //这里要丢异常出去
        return Mono.empty();
    }
}
