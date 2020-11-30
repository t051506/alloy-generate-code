package com.alloy.cloud.plugin.gen;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存数据
 */
public class Cache {

    private static Cache cache = new Cache();
    private Map<String, String> data = new HashMap<>();

    private Cache() {
    }

    public static Cache getInstance() {
        return cache;
    }

    public void set(String k, String v) {
        data.put(k, v);
    }

    public String get(String k) {
        return data.get(k);
    }

    public static boolean isEmpty(String k) {
        String v = cache.data.get(k);
        return v == null || "".equals(v);
    }

    public static boolean isNotEmpty(String k) {
        return !isEmpty(k);
    }

    public void clear() {
        data.clear();
    }
}
