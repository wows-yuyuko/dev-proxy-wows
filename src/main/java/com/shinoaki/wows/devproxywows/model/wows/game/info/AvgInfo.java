package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.devproxywows.model.wows.source.DamageData;
import com.shinoaki.wows.devproxywows.model.wows.source.WinsData;
import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Xun
 * @date 2023/5/14 20:52 星期日
 */
public record AvgInfo(
        @Schema(description = "场均")
        double damage,
        @Schema(description = "场均颜色信息")
        DamageData damageData,
        @Schema(description = "潜在")
        double scoutingDamage,
        @Schema(description = "胜率")
        double win,
        @Schema(description = "胜率颜色信息")
        WinsData winsData,
        @Schema(description = "战损(k/d)")
        double kd,
        @Schema(description = "发现战舰数")
        double shipsSpotted,
        @Schema(description = "飞机击落数")
        double planesKilled,
        @Schema(description = "主武器潜在")
        double artAgro,
        @Schema(description = "鱼雷潜在")
        double tpdAgro,
        @Schema(description = "平均经验")
        double xp,
        @Schema(description = "平均基础经验(如果有,否则-1)")
        double basicXp) {

    public static AvgInfo to(ShipInfo info) {
        double damage = WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameDamage());
        double wins =WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameWins());
        return new AvgInfo(
                damage,WowsCacheUtils.getDamage(info.shipId(), damage),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameScoutingDamage()),
                wins,
        WowsCacheUtils.getWins(wins),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameKd()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameShipsSpotted()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gamePlanesKilled()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameArtAgro()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameTpdAgro()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameXp()),
        WowsCacheUtils.doubleCheckAnd_HALF_UP(info.gameBasicXp())
        );
    }
}
