package com.shinoaki.wows.devproxywows.model.wows;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Xun
 * @date 2023/5/14 15:21 星期日
 */
public record WowsClanInfo(
        @Schema(description = "公会id,0表示没有公会")
        long clanId,
        @Schema(description = "公会标签")
        String tag,
        @Schema(description = "段位颜色rgb")
        String color
) {
    public static WowsClanInfo empty() {
        return new WowsClanInfo(0,  "", "#FFFAFA");
    }
}
