package com.shinoaki.wows.devproxywows.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.codec.HttpSend;
import com.shinoaki.wows.api.codec.http.WowsHttpClanTools;
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
import java.nio.file.FileSystemException;
import java.util.HashMap;
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

    @Operation(summary = "返回请求地址", description = "dev类型下建议使用这个返回的数据请求后去调用解析接口,vortex默认返回PVP数据,需要其他的自行关键词替换")
    @GetMapping(value = "user/ship/list/uri/{server}/server/{accountId}")
    public Mono<Map<String, String>> shipListUri(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server,
                                                 @PathVariable @Parameter(example = "2022515210", description = "账号ID") long accountId) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (code != null) {
            JsonUtils utils = new JsonUtils();
            WowsHttpShipTools tools = new WowsHttpShipTools(utils, client, code, accountId);
            WowsHttpClanTools clanTools = new WowsHttpClanTools(utils, client, code);
            Map<String, String> map = new HashMap<>();
            map.put("dev", tools.developers(this.wowsConfig.getPublicKey()).shipListUri().toString());
            map.put("devClan", clanTools.developers(this.wowsConfig.getPublicKey()).userSearchClanDevelopersUri(accountId).toString());
            map.put("vortex", tools.vortex().shipListUri(WowsBattlesType.PVP).toString());
            map.put("vortexClan", clanTools.vortex().userSearchClanVortexUri(accountId).toString());
            return Mono.just(map);
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }


    @Operation(summary = "查询用户信息和战舰信息", description = "本接口不支持国服,国服请走上传解析,服务器列表:asia,eu,na")
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
                    return this.wowsProcessService.userInfoDev(utils, accountId, node, shipId, this.wowsCache.clansAccountInfo(accountId));
                }
            }
            WowsHttpShipTools tools = new WowsHttpShipTools(utils, client, code, accountId);
            WowsHttpClanTools clanTools = new WowsHttpClanTools(utils, client, code);
            try {
                JsonNode node = utils.parse(HttpSend.sendGet(client, tools.developers(this.wowsConfig.getKey()).shipListUri()));
                JsonNode clan = utils.parse(HttpSend.sendGet(client, clanTools.developers(this.wowsConfig.getKey()).userSearchClanDevelopersUri(accountId)));
                this.wowsCache.shipsStats(accountId, node);
                this.wowsCache.clansAccountInfo(accountId, clan);
                return this.wowsProcessService.userInfoDev(utils, accountId, node, shipId, clan);
            } catch (IOException | HttpStatusException e) {
                return Mono.error(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Mono.error(e);
            }
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }


    @Operation(summary = "上传解析用户数据", description = "根据请求地址给的类型url上传来解析,你也可以不走请求地址而且本地直接生成那个url地址,请求返回的数据不需要处理直接传给服务器解析")
    @PostMapping(value = "user/info/{server}/upload/{dataType}/data/{battleType}/battle/{accountId}/query/{shipId}", consumes =
            MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<UserInfoVO> userData(@PathVariable @Parameter(example = "asia", description = "所属服务器") String server, @PathVariable @Parameter(example =
            "vortex", description = "数据类型[vortex,dev]") String dataType, @PathVariable @Parameter(example = "PVP", description = "数战斗数据类型[PVP,PVP_SOLO," +
            "RANK_SOLO]全大写等") String battleType, @PathVariable @Parameter(example = "2022515210", description = "账号ID") long accountId,
                                     @PathVariable @Parameter(example = "4276041424", description = "战舰ID") long shipId, @Parameter(description = "用户船列表接口的数据" +
            ".json格式") @RequestPart(value = "files") FilePart userInfo,
                                     @Parameter(description = "公会信息.json格式-不想用这个可以传个空的txt文件") @RequestPart(value = "clan") FilePart clan) {
        WowsServer code = WowsServer.findCodeByNull(server);
        if (!userInfo.filename().endsWith(".json")) {
            return Mono.error(new FileSystemException("文件格式异常!请使用json文件格式上传"));
        }
        if (code != null) {
            JsonUtils utils = new JsonUtils();
            try {
                var node = utils.parse(PathUtils.temp(userInfo));
                if (userInfo.filename().endsWith(".json")) {
                    var clanNode = utils.parse(PathUtils.temp(clan));
                    if ("dev".equalsIgnoreCase(dataType)) {
                        return this.wowsProcessService.userInfoDev(utils, accountId, node, shipId, clanNode);
                    } else {
                        return this.wowsProcessService.userInfoVortex(accountId, WowsBattlesType.findCode(battleType), node, shipId, clanNode);
                    }
                }
                if ("dev".equalsIgnoreCase(dataType)) {
                    return this.wowsProcessService.userInfoDev(utils, accountId, node, shipId, null);
                } else {
                    return this.wowsProcessService.userInfoVortex(accountId, WowsBattlesType.findCode(battleType), node, shipId, null);
                }
            } catch (IOException e) {
                return Mono.error(e);
            }
        }
        return Mono.error(new HttpStatusException("服务器code不存在"));
    }
}
