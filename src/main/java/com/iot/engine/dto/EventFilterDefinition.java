package com.iot.engine.dto;

public class EventFilterDefinition {

    private String eventType;
    private String deviceIdPattern;

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDeviceIdPattern() {
        return deviceIdPattern;
    }

    public void setDeviceIdPattern(String deviceIdPattern) {
        this.deviceIdPattern = deviceIdPattern;
    }
}
