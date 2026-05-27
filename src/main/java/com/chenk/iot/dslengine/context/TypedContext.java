package com.chenk.iot.dslengine.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @date 2026/05/26
 **/
public class TypedContext {
    private final ConcurrentMap<Key<?>, Object> store = new ConcurrentHashMap<>();

    public <T> void set(Key<T> key, T value) {
        if (value != null && !key.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException(
                    String.format("Type mismatch: expected %s but got %s",
                            key.getType(), value.getClass()));
        }
        store.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        return (T) store.get(key);
    }

    // 用于表达式求值，将当前存储转换为普通 Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Key<?>, Object> entry : store.entrySet()) {
            map.put(entry.getKey().getName(), entry.getValue());
        }
        return map;
    }
}