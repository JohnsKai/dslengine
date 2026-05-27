package com.chenk.iot.dslengine.node;

import com.chenk.iot.dslengine.context.Key;
import com.chenk.iot.dslengine.context.TypedContext;
import com.chenk.iot.dslengine.engine.NodeFactory;
import com.chenk.iot.dslengine.event.EventFilter;
import com.chenk.iot.dslengine.event.IotEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.iot.engine.dto.ActionDefinition;
import com.iot.engine.dto.BindingDefinition;
import com.iot.engine.dto.EventFilterDefinition;
import com.iot.engine.dto.GenericEventConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @date 2026/05/26
 **/
public class GenericEventNode implements EventNode {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String EVENT_DATA_PREFIX = "event.data.";

    private final NodeFactory nodeFactory = new NodeFactory();

    private EventFilter eventFilter;
    private List<BindingDefinition> bindings = Collections.emptyList();
    private Expression conditionExpr;
    private List<ActionNode> actionSequence = Collections.emptyList();

    public void setConfigDto(GenericEventConfig config) {
        if (config == null) {
            clearConfig();
            return;
        }

        EventFilterDefinition filterDef = config.getEventFilter();
        if (filterDef != null) {
            this.eventFilter = new EventFilter(filterDef.getEventType(), filterDef.getDeviceIdPattern());
        } else {
            this.eventFilter = null;
        }

        if (config.getBindings() != null) {
            this.bindings = Collections.unmodifiableList(new ArrayList<>(config.getBindings()));
        } else {
            this.bindings = Collections.emptyList();
        }

        String condition = config.getCondition();
        if (condition != null && !condition.trim().isEmpty()) {
            this.conditionExpr = AviatorEvaluator.compile(condition);
        } else {
            this.conditionExpr = null;
        }

        this.actionSequence = buildActionSequence(config.getActionSequence());
    }

    @Override
    public void setConfigDto(Object configDto) {
        if (configDto == null) {
            clearConfig();
            return;
        }
        if (configDto instanceof GenericEventConfig) {
            setConfigDto((GenericEventConfig) configDto);
            return;
        }
        if (configDto instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) configDto;
            setConfigDto(MAPPER.convertValue(map, GenericEventConfig.class));
            return;
        }
        throw new IllegalArgumentException(
                "Expected GenericEventConfig or Map but got " + configDto.getClass().getName());
    }

    @Override
    public void onEvent(IotEvent event, TypedContext context) {
        if (event == null) {
            return;
        }
        if (eventFilter != null && !eventFilter.matches(event.getDeviceId(), event.getEventType())) {
            return;
        }

        applyBindings(event, context);

        if (conditionExpr != null && !evaluateCondition(context)) {
            return;
        }

        for (ActionNode action : actionSequence) {
            action.perform(context);
        }
    }

    private void clearConfig() {
        eventFilter = null;
        bindings = Collections.emptyList();
        conditionExpr = null;
        actionSequence = Collections.emptyList();
    }

    private List<ActionNode> buildActionSequence(List<ActionDefinition> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            return Collections.emptyList();
        }
        List<ActionNode> actions = new ArrayList<>(definitions.size());
        for (ActionDefinition definition : definitions) {
            Object node = nodeFactory.create(definition.getType(), definition.getConfig());
            if (!(node instanceof ActionNode)) {
                throw new IllegalArgumentException(
                        "Action sequence entry is not an ActionNode: " + definition.getType());
            }
            actions.add((ActionNode) node);
        }
        return Collections.unmodifiableList(actions);
    }

    private void applyBindings(IotEvent event, TypedContext context) {
        for (BindingDefinition binding : bindings) {
            Object value = resolveBindingValue(event, binding.getFrom());
            context.set(Key.of(binding.getTo(), Object.class), value);
        }
    }

    private Object resolveBindingValue(IotEvent event, String from) {
        if (from == null) {
            return null;
        }
        switch (from) {
            case "event.deviceId":
                return event.getDeviceId();
            case "event.eventType":
                return event.getEventType();
            default:
                if (from.startsWith(EVENT_DATA_PREFIX)) {
                    String key = from.substring(EVENT_DATA_PREFIX.length());
                    return event.getData().toMap().get(key);
                }
                throw new IllegalArgumentException("Unsupported binding path: " + from);
        }
    }

    private boolean evaluateCondition(TypedContext context) {
        Object result = conditionExpr.execute(context.toMap());
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result instanceof Number) {
            return ((Number) result).doubleValue() != 0;
        }
        return result != null;
    }
}
