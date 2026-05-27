package com.chenk.iot.dslenginecore.node;

import com.chenk.iot.dslenginecore.context.TypedContext;
import com.chenk.iot.dslenginecore.event.IotEvent;

/**
 * @date 2026/05/26
 **/
public interface EventNode {
    void onEvent(IotEvent event, TypedContext context);

    default void setConfigDto(Object configDto) {
    }
}
