package com.chenk.iot.dto;

import java.util.Map;

public class NodeDefinition {

    private String id;
    private String type;
    private Map<String, Object> config;
    private String next;
    private String nextTrue;
    private String nextFalse;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getNextTrue() {
        return nextTrue;
    }

    public void setNextTrue(String nextTrue) {
        this.nextTrue = nextTrue;
    }

    public String getNextFalse() {
        return nextFalse;
    }

    public void setNextFalse(String nextFalse) {
        this.nextFalse = nextFalse;
    }
}
