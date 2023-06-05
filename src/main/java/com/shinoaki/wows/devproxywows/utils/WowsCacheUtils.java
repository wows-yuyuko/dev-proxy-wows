package com.shinoaki.wows.devproxywows.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.shinoaki.wows.api.pr.PrData;
import com.shinoaki.wows.api.utils.JsonUtils;
import com.shinoaki.wows.devproxywows.model.wows.PrInfo;
import com.shinoaki.wows.devproxywows.model.wows.WowsShipInfo;
import com.shinoaki.wows.devproxywows.model.wows.source.DamageData;
import com.shinoaki.wows.devproxywows.model.wows.source.WinsData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author Xun
 * @date 2023/6/5 16:26 星期一
 */
public class WowsCacheUtils {
    private WowsCacheUtils() {

    }

    private static final Map<String, List<DamageData>> COLOR_DAMAGE_LIST = new HashMap<>();
    private static final List<WinsData> COLOR_WINS_LIST = new ArrayList<>();

    private static final List<PrInfo> PR_INFO_LIST = new ArrayList<>();
    private static final Map<Long, WowsShipInfo> SHIP_MAP = new TreeMap<>();
    private static final Map<Long, PrData> PR_SERVER_MAP = new TreeMap<>();

    public static WowsShipInfo getShipMap(long shipId) {
        return SHIP_MAP.getOrDefault(shipId, WowsShipInfo.empty(shipId));
    }

    public static PrData getPr(long shipId) {
        return PR_SERVER_MAP.getOrDefault(shipId, PrData.empty());
    }

    public static PrInfo getPr(int pr) {
        if (pr <= 0) {
            return PR_INFO_LIST.get(0);
        }
        for (var x : PR_INFO_LIST) {
            if (pr < x.value()) {
                return x;
            }
        }
        return PR_INFO_LIST.get(PR_INFO_LIST.size() - 1);
    }

    public static DamageData getDamage(long shipId, double value) {
        List<DamageData> list = COLOR_DAMAGE_LIST.getOrDefault(WowsCacheUtils.getShipMap(shipId).shipType(), List.of());
        if (list.isEmpty()) {
            return DamageData.empty();
        }
        if (value <= 0) {
            return list.get(0);
        }
        for (var x : list) {
            if (value < x.value()) {
                return x;
            }
        }
        return list.get(list.size() - 1);
    }

    public static WinsData getWins(double value) {
        if (value <= 0.0) {
            return COLOR_WINS_LIST.get(0);
        }
        for (var x : COLOR_WINS_LIST) {
            if (value < x.value()) {
                return x;
            }
        }
        return COLOR_WINS_LIST.get(COLOR_WINS_LIST.size() - 1);
    }


    /**
     * 双精度溢出验证-外加小数点后两位
     *
     * @param data 双精度数值
     * @return NaN等全部返回0
     */
    public static double doubleCheckAnd_HALF_UP(double data) {
        if (Double.isInfinite(data)) {
            return 0.0;
        } else if (Double.isNaN(data)) {
            return 0.0;
        } else if (data <= 0.0) {
            return 0.0;
        } else {
            return BigDecimal.valueOf(data).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }
    }

    public static void init(String path) throws IOException {
        JsonUtils utils = new JsonUtils();
        loadWowsShipInfo(new File(path + "WowsShipInfo.json"), utils);
        loadWowsPrData(new File(path + "PrData.json"), utils);
        loadPr(new File(path + "Pr.json"), utils);
        loadDamage(new File(path + "Damage.json"), utils);
        loadWins(new File(path + "Wins.json"), utils);
    }

    private static void loadWowsPrData(File file, JsonUtils utils) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            loadWowsPrData(utils.parse(new String(in.readAllBytes()), new TypeReference<Map<Long, PrData>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }));
        }
    }

    private static void loadWowsShipInfo(File file, JsonUtils utils) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            loadWowsShipInfo(utils.parse(new String(in.readAllBytes()), new TypeReference<List<WowsShipInfo>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }));
        }
    }


    private static void loadPr(File file, JsonUtils utils) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            loadPrInfo(utils.parse(new String(in.readAllBytes()), new TypeReference<List<PrInfo>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }));
        }
    }

    private static void loadDamage(File file, JsonUtils utils) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            loadDamage(utils.parse(new String(in.readAllBytes()), new TypeReference<Map<String, List<DamageData>>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }));
        }
    }

    private static void loadWins(File file, JsonUtils utils) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            loadWins(utils.parse(new String(in.readAllBytes()), new TypeReference<List<WinsData>>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            }));
        }
    }

    private static void loadWowsPrData(Map<Long, PrData> map) {
        PR_SERVER_MAP.putAll(map);
    }

    private static void loadWowsShipInfo(List<WowsShipInfo> info) {
        info.forEach(x -> SHIP_MAP.put(x.shipId(), x));
    }

    private static void loadPrInfo(List<PrInfo> info) {
        PR_INFO_LIST.clear();
        PR_INFO_LIST.addAll(info);
    }

    private static void loadDamage(Map<String, List<DamageData>> info) {
        COLOR_DAMAGE_LIST.clear();
        COLOR_DAMAGE_LIST.putAll(info);
    }

    private static void loadWins(List<WinsData> info) {
        COLOR_WINS_LIST.clear();
        COLOR_WINS_LIST.addAll(info);
    }
}
