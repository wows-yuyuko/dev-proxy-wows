package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Xun
 * @date 2023/5/14 20:56 星期日
 */
public record BattleInfo(
        @Schema(description = "总场次")
        int battle,
        @Schema(description = "胜利场次")
        int wins,
        @Schema(description = "失败场次")
        int losses,
        @Schema(description = "存活场次")
        int survived,
        @Schema(description = "胜利并且存活的场次")
        int winAndSurvived
) {

    public static BattleInfo to(ShipInfo info) {
        return new BattleInfo(info.battle().battle(),
                info.battle().wins(),
                info.battle().losses(),
                info.battle().survived(),
                info.battle().winAndSurvived());
    }
}
