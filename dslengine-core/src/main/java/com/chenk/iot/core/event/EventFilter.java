package com.chenk.iot.core.event;

import java.util.regex.Pattern;

/**
 * @date 2026/05/26
 **/
public class EventFilter {
    private final String  eventType;
    private final Pattern devicePattern;

    public EventFilter(String eventType, String deviceIdPattern) {
        this.eventType = eventType;
        // 简单将 * 转换为 .*
        String regex = deviceIdPattern.replace("*", ".*");
        this.devicePattern = Pattern.compile(regex);
    }

    public boolean matches(String deviceId, String eventType) {
        return this.eventType.equals(eventType) && devicePattern.matcher(deviceId).matches();
    }
}