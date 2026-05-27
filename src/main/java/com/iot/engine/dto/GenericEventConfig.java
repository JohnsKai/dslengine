package com.iot.engine.dto;

import java.util.List;

public class GenericEventConfig {

    private EventFilterDefinition eventFilter;
    private List<BindingDefinition> bindings;
    private String condition;
    private List<ActionDefinition> actionSequence;

    public EventFilterDefinition getEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(EventFilterDefinition eventFilter) {
        this.eventFilter = eventFilter;
    }

    public List<BindingDefinition> getBindings() {
        return bindings;
    }

    public void setBindings(List<BindingDefinition> bindings) {
        this.bindings = bindings;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public List<ActionDefinition> getActionSequence() {
        return actionSequence;
    }

    public void setActionSequence(List<ActionDefinition> actionSequence) {
        this.actionSequence = actionSequence;
    }
}
