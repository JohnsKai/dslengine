package com.chenk.iot.dslenginecore.node;

import com.chenk.iot.dslenginecore.context.TypedContext;

/**
 * @date 2026/05/26
 **/
public interface ActionNode extends RuleNode {

    void perform(TypedContext context);


    @Override
    default void execute(TypedContext context) {
        perform(context);
    }
}