package com.shinoaki.wows.devproxywows.model.wows.source;

/**
 * @author Xun
 * @date 2023/5/29 7:35 星期一
 */
public record DamageData(
        int code,
        int value,
        String color
) implements Comparable<DamageData> {
    @Override
    public int compareTo(DamageData prJson) {
        return Integer.compare(this.value, prJson.value);
    }

    public static DamageData empty(){
        return new DamageData(0,0,"#FE7903");
    }
}
