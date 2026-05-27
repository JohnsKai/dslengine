package com.iot.engine.dto;

import java.util.List;

public class RuleDefinition {

    private String id;
    private String startNodeId;
    private List<NodeDefinition> nodes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartNodeId() {
        return startNodeId;
    }

    public void setStartNodeId(String startNodeId) {
        this.startNodeId = startNodeId;
    }

    public List<NodeDefinition> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDefinition> nodes) {
        this.nodes = nodes;
    }
}
