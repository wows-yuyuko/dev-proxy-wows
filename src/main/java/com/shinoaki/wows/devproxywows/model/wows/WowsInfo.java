package com.shinoaki.wows.devproxywows.model.wows;

import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.api.pr.PrData;
import com.shinoaki.wows.api.pr.PrUtils;
import com.shinoaki.wows.api.type.WowsBattlesType;
import com.shinoaki.wows.devproxywows.model.wows.game.BattleInfoData;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Xun
 * @date 2023/5/15 1:07 星期一
 */
public record WowsInfo(
        @Schema(description = "战斗类型")
        WowsBattlesType type,
        @Schema(description = "PR信息,code=pr,value=当前PR值,nextValue=下一级所需分数(0表示最高了),name=等级名称中文,englishName=等级名称英文,color=颜色RGB代码")
        PrInfo prInfo,
        @Schema(description = "战舰数据信息")
        BattleInfoData shipInfo) {
    public static WowsInfo prInfo(WowsBattlesType type, PrData server, ShipInfo shipInfo) {
        if (server == null || shipInfo == null) {
            return new WowsInfo(type, PrInfo.pr(0), BattleInfoData.empty(0));
        }
        return new WowsInfo(type, PrInfo.pr(PrUtils.pr(new PrData(shipInfo.damageDealt(), shipInfo.fragsInfo().frags(), shipInfo.battle().wins()),
                server)), BattleInfoData.to(shipInfo));

    }
}
