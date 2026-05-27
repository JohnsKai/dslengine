package com.chenk.iot.core.engine;

import com.chenk.iot.core.node.*;
import com.chenk.iot.dto.GenericEventConfig;
import com.chenk.iot.dto.LogActionConfig;
import com.chenk.iot.dto.MqttActionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Map;

/**
 * @date 2026/05/26
 **/
public class NodeFactory {

    private final ObjectMapper objectMapper;

    public NodeFactory() {
        this(new ObjectMapper());
    }

    public NodeFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 过渡方法：将 Map 配置转换为强类型 DTO 后创建节点。
     */
    public Object create(String type, Map<String, Object> config) {
        return createNode(type, toConfigDto(type, config));
    }

    public Object createNode(String type, Object configDto) {
        String normalizedType = normalizeType(type);
        Object node           = instantiateNode(normalizedType);
        applyConfig(node, configDto);
        return node;
    }

    private Object instantiateNode(String type) {
        switch (type) {
            case "genericevent":
                return new GenericEventNode();
            case "log":
                return new LogActionNode();
            case "mqtt":
                return new MqttActionNode();
            default:
                throw new IllegalArgumentException("Unknown node type: " + type);
        }
    }

    private void applyConfig(Object node, Object configDto) {
        if (node instanceof RuleNode) {
            ((RuleNode) node).setConfigDto(configDto);
        } else if (node instanceof EventNode) {
            ((EventNode) node).setConfigDto(configDto);
        }
    }

    public Object toConfigDto(String type, Map<String, Object> config) {
        if (config == null) {
            return null;
        }
        return objectMapper.convertValue(config, resolveConfigDtoClass(type));
    }

    public Class<?> resolveConfigDtoClass(String type) {
        switch (normalizeType(type)) {
            case "genericevent":
                return GenericEventConfig.class;
            case "log":
                return LogActionConfig.class;
            case "mqtt":
                return MqttActionConfig.class;
            default:
                throw new IllegalArgumentException("Unknown node type: " + type);
        }
    }

    private static String normalizeType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Node type must not be blank");
        }
        return type.trim().toLowerCase(Locale.ROOT);
    }
}
