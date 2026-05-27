package com.chenk.iot.dslengine.context;

import java.util.Objects;

/**
 * @date 2026/05/26
 **/
public class Key<T> {
    private final String name;
    private final Class<T> type;

    private Key(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }

    public static <T> Key<T> of(String name, Class<T> type) {
        return new Key<>(name, type);
    }

    public String getName() { return name; }
    public Class<T> getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        Key<?> key = (Key<?>) o;
        return name.equals(key.name) && type.equals(key.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}