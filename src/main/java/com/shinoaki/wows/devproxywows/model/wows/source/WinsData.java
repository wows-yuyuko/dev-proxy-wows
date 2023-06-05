package com.shinoaki.wows.devproxywows.model.wows.source;

/**
 * @author Xun
 * @date 2023/5/29 7:33 星期一
 */
public record WinsData(
        int code,
        double value,
        String color
) implements Comparable<WinsData> {
    @Override
    public int compareTo(WinsData prJson) {
        return Double.compare(this.value, prJson.value);
    }
}
