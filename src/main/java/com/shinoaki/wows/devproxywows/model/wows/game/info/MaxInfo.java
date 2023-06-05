package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 最高击杀信息
 *
 * @author Xun
 * @date 2023/5/14 17:34 星期日
 */
public record MaxInfo(MaxShipInfo maxFrags, MaxShipInfo maxFragsByMain, MaxShipInfo maxFragsByTpd, MaxShipInfo maxFragsByDbomb, MaxShipInfo maxFragsByRam,
                      MaxShipInfo maxFragsByAtba,
                      MaxShipInfo maxDamageDealtToBuildings, MaxShipInfo maxFragsByPlanes, MaxShipInfo maxDamageDealt, MaxShipInfo maxScoutingDamage,
                      MaxShipInfo maxPlanesKilled,
                      MaxShipInfo maxShipsSpotted, MaxShipInfo maxTotalAgro, MaxShipInfo maxSuppressionsCount, MaxShipInfo maxXp, MaxShipInfo maxBasicXp) {

    public static MaxInfo to(ShipInfo info) {
        return new MaxInfo(
                MaxShipInfo.to(info.maxInfo().maxFrags()),
                MaxShipInfo.to(info.maxInfo().maxFragsByMain()),
                MaxShipInfo.to(info.maxInfo().maxFragsByTpd()),
                MaxShipInfo.to(info.maxInfo().maxFragsByDbomb()),
                MaxShipInfo.to(info.maxInfo().maxFragsByRam()),
                MaxShipInfo.to(info.maxInfo().maxFragsByAtba()),
                MaxShipInfo.to(info.maxInfo().maxDamageDealtToBuildings()),
                MaxShipInfo.to(info.maxInfo().maxFragsByPlanes()),
                MaxShipInfo.to(info.maxInfo().maxDamageDealt()),
                MaxShipInfo.to(info.maxInfo().maxScoutingDamage()),
                MaxShipInfo.to(info.maxInfo().maxPlanesKilled()),
                MaxShipInfo.to(info.maxInfo().maxShipsSpotted()),
                MaxShipInfo.to(info.maxInfo().maxTotalAgro()),
                MaxShipInfo.to(info.maxInfo().maxSuppressionsCount()),
                MaxShipInfo.to(info.maxInfo().maxXp()),
                MaxShipInfo.to(info.maxInfo().maxBasicXp())
        );
    }

    public static MaxInfo empty(long shipId) {
        return new MaxInfo(MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId),
                MaxShipInfo.empty(shipId));
    }


    public record MaxShipInfo(
            @Schema(description = "战舰ID")
            long shipId,
            @Schema(description = "当前战舰某项类型的最高值")
            int value) {
        public static MaxShipInfo to(com.shinoaki.wows.api.data.ship.MaxInfo.MaxShipInfo info) {
            return new MaxShipInfo(info.shipId(), info.value());
        }

        private static MaxShipInfo empty(long shipId) {
            return new MaxShipInfo(shipId, 0);
        }
    }
}
