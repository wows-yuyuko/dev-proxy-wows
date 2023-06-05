package com.shinoaki.wows.devproxywows.model.wows;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author Xun
 * @date 2023/5/15 15:20 星期一
 */
public record WowsShipInfo(
        @Schema(description = "战舰ID")
        long shipId,
        @Schema(description = "名称-中文")
        String nameCn,
        @Schema(description = "名称-英文")
        String nameEnglish,
        @Schema(description = "等级")
        int level,
        @Schema(description = "战舰类型")
        String shipType,
        @Schema(description = "所属国家或地区")
        String country,
        @Schema(description = "图片-需要判断是否是null", nullable = true)
        String imgSmall,
        @Schema(description = "图片-需要判断是否是null", nullable = true)
        String imgLarge,
        @Schema(description = "图片-需要判断是否是null", nullable = true)
        String imgMedium,
        @Schema(description = "索引id")
        String shipIndex,
        @Schema(description = "属性分组")
        String groupType
) {

    public static WowsShipInfo empty(long shipId) {
        return new WowsShipInfo(shipId, "未知战舰", "none", 0, "none", "none", null, null, null, "none", "none");
    }
}
