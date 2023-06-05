package com.shinoaki.wows.devproxywows.model.wows.game.info;

import com.shinoaki.wows.api.data.ShipInfo;
import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 站点信息
 *
 * @author Xun
 * @date 2023/5/14 17:50 星期日
 */
public record ControlCapturedAndDroppedPointsInfo(@Schema(description = "占领贡献率") double gameContributionToCapture,
                                                  @Schema(description = "防御贡献率") double gameContributionToDefense) {

    public static ControlCapturedAndDroppedPointsInfo to(ShipInfo info) {
        return new ControlCapturedAndDroppedPointsInfo(WowsCacheUtils.doubleCheckAnd_HALF_UP(info.controlCapturedAndDroppedPoints().gameContributionToCapture()), WowsCacheUtils.doubleCheckAnd_HALF_UP(info.controlCapturedAndDroppedPoints().gameContributionToDefense()));
    }
}
