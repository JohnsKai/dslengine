package com.chenk.iot.core.node;

import com.chenk.iot.core.context.TypedContext;
import com.chenk.iot.dto.MqttActionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @date 2026/05/26
 **/
public class MqttActionNode implements ActionNode {

    private static final Logger log = LoggerFactory.getLogger(MqttActionNode.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String id;
    private MqttActionConfig config;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setConfigDto(MqttActionConfig config) {
        this.config = config;
    }

    @Override
    public void setConfigDto(Object configDto) {
        if (configDto == null) {
            this.config = null;
            return;
        }
        if (configDto instanceof MqttActionConfig) {
            setConfigDto((MqttActionConfig) configDto);
            return;
        }
        if (configDto instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) configDto;
            setConfigDto(MAPPER.convertValue(map, MqttActionConfig.class));
            return;
        }
        throw new IllegalArgumentException(
                "Expected MqttActionConfig or Map but got " + configDto.getClass().getName());
    }

    /**
     * 本地模拟控制设备耗时10ms
     * @param context
     */
    @Override
    public void perform(TypedContext context) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (config != null) {
            log.debug("MQTT publish topic={} payload={} qos={}",
                    config.getTopic(), config.getPayload(), config.getQos());
        }
    }
}
