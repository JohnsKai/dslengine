package com.chenk.iot.core.node;

import com.chenk.iot.core.context.TypedContext;

/**
 * @date 2026/05/26
 **/
public interface ConditionNode extends RuleNode {
    boolean evaluate(TypedContext context);
    @Override
    default void execute(TypedContext context) {
        throw new UnsupportedOperationException("ConditionNode should not call execute()");
    }
}