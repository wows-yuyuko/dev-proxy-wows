package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 击杀信息
 *
 * @author Xun
 * @date 2023/5/14 17:32 星期日
 */
public record FragsInfo(
        @Schema(description = "总击杀")
        int frags,
        @Schema(description = "主炮击杀")
        int fragsByMain,
        @Schema(description = "副炮击杀")
        int fragsByAtba,
        @Schema(description = "舰载机击杀")
        int fragsByPlanes,
        @Schema(description = "鱼雷击杀")
        int fragsByTpd,
        @Schema(description = "撞击击沉")
        int fragsByRam,
        @Schema(description = "深水炸弹击杀-注意目前深弹不算起火和进水")
        int fragsByDbomb
) {
    public static FragsInfo to(ShipInfo info) {
        return new FragsInfo(
                info.fragsInfo().frags(),
                info.fragsInfo().fragsByMain(),
                info.fragsInfo().fragsByAtba(),
                info.fragsInfo().fragsByPlanes(),
                info.fragsInfo().fragsByTpd(),
                info.fragsInfo().fragsByRam(),
                info.fragsInfo().fragsByDbomb()
        );
    }
}
