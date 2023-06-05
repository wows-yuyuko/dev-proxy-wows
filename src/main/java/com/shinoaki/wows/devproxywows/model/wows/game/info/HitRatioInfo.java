package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 命中信息
 *
 * @author Xun
 * @date 2023/5/14 17:49 星期日
 */
public record HitRatioInfo(
        @Schema(description = "主炮命中率")
        double ratioMain,
        @Schema(description = "副炮命中率")
        double ratioAtba,
        @Schema(description = "鱼雷命中率")
        double ratioTpd,
        @Schema(description = "深水炸弹命中率")
        double ratioTbomb
) {

    public static HitRatioInfo to(ShipInfo info) {
        return new HitRatioInfo(
                WowsCacheUtils.doubleCheckAnd_HALF_UP(info.ratioMain().hitRatio()),
                WowsCacheUtils.doubleCheckAnd_HALF_UP(info.ratioAtba().hitRatio()),
                WowsCacheUtils.doubleCheckAnd_HALF_UP(info.ratioTpd().hitRatio()),
                WowsCacheUtils.doubleCheckAnd_HALF_UP(info.ratioTbomb().hitRatio())
        );
    }
}
