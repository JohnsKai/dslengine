package com.chenk.iot.core.engine;

import com.chenk.iot.core.node.LogActionNode;
import com.chenk.iot.core.node.MqttActionNode;
import com.chenk.iot.dto.NodeDefinition;
import com.chenk.iot.dto.RuleDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @date 2026/05/26
 **/
public class RuleEngine {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NodeFactory  nodeFactory  = new NodeFactory(objectMapper);

    private RuleDefinition              ruleDefinition;
    private Map<String, NodeDefinition> nodeDefinitions = Collections.emptyMap();
    private Map<String, Object>         nodeInstances   = Collections.emptyMap();

    public void registerRule(String ruleJson) throws IOException {
        RuleDefinition rule = objectMapper.readValue(ruleJson, RuleDefinition.class);

        Map<String, NodeDefinition> definitions = new LinkedHashMap<>();
        Map<String, Object>         instances   = new LinkedHashMap<>();

        if (rule.getNodes() != null) {
            for (NodeDefinition nodeDef : rule.getNodes()) {
                definitions.put(nodeDef.getId(), nodeDef);
                Object configDto = nodeFactory.toConfigDto(nodeDef.getType(), nodeDef.getConfig());
                Object node      = nodeFactory.createNode(nodeDef.getType(), configDto);
                assignNodeId(node, nodeDef.getId());
                instances.put(nodeDef.getId(), node);
            }
        }

        this.ruleDefinition = rule;
        this.nodeDefinitions = Collections.unmodifiableMap(definitions);
        this.nodeInstances = Collections.unmodifiableMap(instances);
    }

    public RuleDefinition getRuleDefinition() {
        return ruleDefinition;
    }

    public Map<String, NodeDefinition> getNodeDefinitions() {
        return nodeDefinitions;
    }

    public Map<String, Object> getNodeInstances() {
        return nodeInstances;
    }

    public String getStartNodeId() {
        return ruleDefinition != null ? ruleDefinition.getStartNodeId() : null;
    }

    private void assignNodeId(Object node, String id) {
        if (node instanceof LogActionNode) {
            ((LogActionNode) node).setId(id);
        } else if (node instanceof MqttActionNode) {
            ((MqttActionNode) node).setId(id);
        }
    }
}
