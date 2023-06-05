package com.shinoaki.wows.devproxywows.model.valid;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Xun
 * @date 2023/6/5 11:07 星期一
 */
public record UserInfoValid(
        @Schema(description = "战斗类型,[PVP,PVE,CLAN]等")
        String battleType,
        @Schema(description = "对局载入时间,或数据上传时间-毫秒时间戳")
        long time,
        @Schema(description = "对局信息")
        List<BattleInfo> infoList) {

    public record BattleInfo(
            @Schema(description = "玩家所属服务器")
            String server,
            @Schema(description = "玩家账号ID,机器人为0")
            long accountId,
            @Schema(description = "玩家账号名称")
            String userName,
            @Schema(description = "玩家所使用的的舰船ID")
            long shipId,
            @Schema(description = "玩家是否隐藏了战绩")
            boolean hidden,
            @Schema(description = "玩家所属公会,为0表示没有")
            long clanId,
            @Schema(description = "公会tag 玩家没有公会则是null")
            String tag,
            @Schema(description = "玩家对局team")
            int relation) {

    }
}
