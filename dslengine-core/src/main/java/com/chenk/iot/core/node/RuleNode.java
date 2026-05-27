package com.chenk.iot.core.node;

import com.chenk.iot.core.context.TypedContext;

/**
 * @date 2026/05/26
 **/
public interface RuleNode {
    void execute(TypedContext context);
    String getId();

    default void setConfigDto(Object configDto) {
    }
}
