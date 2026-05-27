package com.chenk.iot.dslengine.node;

import com.chenk.iot.dslengine.context.TypedContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.engine.dto.LogActionConfig;

import java.util.Map;

/**
 * @date 2026/05/26
 **/
public class LogActionNode implements ActionNode {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String id;
    private LogActionConfig config;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConfigDto(LogActionConfig config) {
        this.config = config;
    }

    @Override
    public void setConfigDto(Object configDto) {
        if (configDto == null) {
            this.config = null;
            return;
        }
        if (configDto instanceof LogActionConfig) {
            setConfigDto((LogActionConfig) configDto);
            return;
        }
        if (configDto instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) configDto;
            setConfigDto(MAPPER.convertValue(map, LogActionConfig.class));
            return;
        }
        throw new IllegalArgumentException(
                "Expected LogActionConfig or Map but got " + configDto.getClass().getName());
    }

    @Override
    public void perform(TypedContext context) {
        // TODO: log using config.getMessage()
    }
}
