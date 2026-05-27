package com.chenk.iot.dslenginecore.event;

import com.chenk.iot.dslenginecore.context.TypedContext;

/**
 * @date 2026/05/26
 **/
public class IotEvent {
    private final String deviceId;
    private final String eventType;
    private final TypedContext data;

    public IotEvent(String deviceId, String eventType, TypedContext data) {
        this.deviceId = deviceId;
        this.eventType = eventType;
        this.data = data;
    }

    public String getDeviceId() { return deviceId; }
    public String getEventType() { return eventType; }
    public TypedContext getData() { return data; }
}