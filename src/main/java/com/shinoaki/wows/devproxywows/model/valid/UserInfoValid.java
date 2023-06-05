package com.shinoaki.wows.devproxywows.model.valid;

import java.util.List;

/**
 * @author Xun
 * @date 2023/6/5 11:07 星期一
 */
public record UserInfoValid(String battleType, long time, List<BattleInfo> infoList) {

    public static record BattleInfo(String server, long accountId, String userName, long shipId, boolean hidden, long clanId, String tag, int relation) {

    }
}
