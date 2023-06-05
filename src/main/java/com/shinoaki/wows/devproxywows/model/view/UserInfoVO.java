package com.shinoaki.wows.devproxywows.model.view;

import com.shinoaki.wows.devproxywows.model.wows.WowsClanInfo;
import com.shinoaki.wows.devproxywows.model.wows.WowsInfo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Xun
 * @date 2023/6/5 16:07 星期一
 */
public record UserInfoVO(
        @Schema(description = "账号ID,-1表示隐藏了战绩")
        long accountId,
        @Schema(description = "公会信息")
        WowsClanInfo clanInfo,
        List<WowsInfo> userInfo,

        @Schema(description = "战舰信息")
        List<WowsInfo> shipInfo
) {
}
