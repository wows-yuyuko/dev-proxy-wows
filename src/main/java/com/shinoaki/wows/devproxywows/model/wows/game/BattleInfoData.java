package com.shinoaki.wows.devproxywows.model.wows.game;

import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.devproxywows.model.wows.game.info.*;
import com.shinoaki.wows.devproxywows.model.wows.source.DamageData;
import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 战斗数据信息
 *
 * @author Xun
 * @date 2023/5/14 17:32 星期日
 */
public record BattleInfoData(
        @Schema(description = "场次数据")
        BattleInfo battleInfo,
        @Schema(description = "平均值数据")
        AvgInfo avgInfo,
        @Schema(description = "击杀数据")
        FragsInfo fragsInfo,
        @Schema(description = "最高记录数据")
        MaxInfo maxInfo,
        @Schema(description = "命中率数据")
        HitRatioInfo hitRatioInfo,
        @Schema(description = "最后战斗时间-秒")
        long lastBattleTime
) {
    public static BattleInfoData to(ShipInfo info) {
        return new BattleInfoData(
                BattleInfo.to(info),
                AvgInfo.to(info),
                FragsInfo.to(info),
                MaxInfo.to(info),

                HitRatioInfo.to(info),
                info.lastBattleTime()
        );
    }

    public static BattleInfoData empty(long shipId) {
        return new BattleInfoData(new BattleInfo(0, 0, 0, 0, 0),
                new AvgInfo(0, DamageData.empty(), 0, 0, WowsCacheUtils.getWins(0), 0, 0, 0, 0, 0, 0, 0),
                new FragsInfo(0, 0, 0, 0, 0, 0, 0),
                MaxInfo.empty(shipId),
                new HitRatioInfo(0, 0, 0, 0), 0);
    }
}
