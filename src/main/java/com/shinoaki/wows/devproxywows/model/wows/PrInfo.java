package com.shinoaki.wows.devproxywows.model.wows;

import com.shinoaki.wows.devproxywows.utils.WowsCacheUtils;

/**
 * @author Xun
 * @date 2023/5/11 23:38 星期四
 */
public record PrInfo(
        int code,
        int value,
        int nextValue,
        String name,
        String englishName,
        String color
) implements Comparable<PrInfo> {

    public static PrInfo pr(int pr) {
        PrInfo info = WowsCacheUtils.getPr(pr);
        return new PrInfo(info.code(), pr, pr > info.value() ? 0 : info.value() - pr, info.name, info.englishName, info.color);
    }

    @Override
    public int compareTo(PrInfo prJson) {
        return this.value - prJson.value;
    }
}
