package com.shinoaki.wows.devproxywows.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.api.developers.DevelopersUserShip;
import com.shinoaki.wows.api.developers.clan.DevelopersSearchUserClan;
import com.shinoaki.wows.api.error.StatusException;
import com.shinoaki.wows.api.pr.PrData;
import com.shinoaki.wows.api.type.WowsBattlesType;
import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.api.vortex.VortexUserShip;
import com.shinoaki.wows.api.vortex.clan.account.VortexSearchClanUser;
import com.shinoaki.wows.devproxywows.model.view.UserInfoVO;
import com.shinoaki.wows.devproxywows.model.wows.WowsClanInfo;
import com.shinoaki.wows.devproxywows.model.wows.WowsInfo;
import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xun
 * @date 2023/6/5 15:59 星期一
 */
@Service
public class WowsProcessService {
    public static final Logger log = LoggerFactory.getLogger(WowsProcessService.class);

    /**
     * 每晚1点更新数据
     */
    @PostConstruct
    @Scheduled(cron = "0 0 1 * * ?")
    public void dataUpdate() {
        try {
            WowsCacheUtils.init(System.getProperty("user.dir") + File.separator + "config" + File.separator);
            log.info("数据加载完成...");
        } catch (IOException e) {
            log.error("加载缓存文件异常!", e);
        }
    }

    public Mono<UserInfoVO> userInfoDev(JsonUtils utils, long accountId, JsonNode node, long shipId, JsonNode clan) {
        try {
            DevelopersUserShip ship = DevelopersUserShip.parse(node);
            if (ship.accountId() <= 0) {
                //隐藏了战绩,或者是查询不到
                return Mono.just(new UserInfoVO(-1, WowsClanInfo.empty(), List.of(), List.of()));
            }
            return userInfo(accountId, shipId, ship.toShipInfoMap(), clanDev(accountId, utils, clan));
        } catch (StatusException | JsonProcessingException e) {
            return Mono.error(e);
        }
    }

    public Mono<UserInfoVO> userInfoVortex(long accountId, WowsBattlesType type, JsonNode node, long shipId, JsonNode clan) {
        try {
            VortexUserShip ship = VortexUserShip.parse(type, node);
            if (ship.hiddenProfile()) {
                //隐藏了战绩,或者是查询不到
                return Mono.just(new UserInfoVO(-1, WowsClanInfo.empty(), List.of(), List.of()));
            }
            return userInfo(accountId, shipId, Map.of(type, ship.toShipInfoList()), clanVortex(clan));
        } catch (StatusException | JsonProcessingException e) {
            return Mono.error(e);
        }
    }


    private Mono<UserInfoVO> userInfo(long accountId, long shipId, Map<WowsBattlesType, List<ShipInfo>> shipInfoMap, WowsClanInfo clanInfo) {
        Map<WowsBattlesType, ShipInfo> infoMap = new EnumMap<>(WowsBattlesType.class);
        Map<WowsBattlesType, PrData> prMap = new EnumMap<>(WowsBattlesType.class);
        Map<WowsBattlesType, ShipInfo> shipMap = new EnumMap<>(WowsBattlesType.class);
        for (var entry : shipInfoMap.entrySet()) {
            ShipInfo userInfo = null;
            ShipInfo shipInfo = null;
            PrData server = PrData.empty();
            for (var list : entry.getValue()) {
                if (userInfo == null) {
                    userInfo = list;
                } else {
                    userInfo = userInfo.addition(list);
                }
                server = server.addition(list.battle().battle(), WowsCacheUtils.getPr(list.shipId()));
                if (list.shipId() == shipId) {
                    shipInfo = list;
                }
            }
            infoMap.put(entry.getKey(), userInfo);
            prMap.put(entry.getKey(), server);
            shipMap.put(entry.getKey(), shipInfo);
        }
        //计算
        List<WowsInfo> userInfoList = new ArrayList<>();
        List<WowsInfo> shipInfoList = new ArrayList<>();
        for (var entry : infoMap.entrySet()) {
            userInfoList.add(WowsInfo.prInfo(entry.getKey(), prMap.get(entry.getKey()), entry.getValue()));
        }
        for (var entry : shipMap.entrySet()) {
            shipInfoList.add(WowsInfo.prInfo(entry.getKey(), prMap.get(entry.getKey()), entry.getValue()));
        }
        return Mono.just(new UserInfoVO(accountId, clanInfo, userInfoList, shipInfoList));
    }

    private WowsClanInfo clanVortex(JsonNode node) throws StatusException {
        if (node == null) {
            return WowsClanInfo.empty();
        }
        VortexSearchClanUser user = VortexSearchClanUser.to(node);
        return new WowsClanInfo(user.clan_id(), user.clan().tag(), user.clan().color());
    }

    private WowsClanInfo clanDev(long accountId, JsonUtils utils, JsonNode node) throws StatusException, JsonProcessingException {
        if (node == null) {
            return WowsClanInfo.empty();
        }
        DevelopersSearchUserClan user = DevelopersSearchUserClan.parse(utils, accountId, node.toString());
        return new WowsClanInfo(user.clan_id(), user.clan().tag(), "#FFFAFA");
    }
}
