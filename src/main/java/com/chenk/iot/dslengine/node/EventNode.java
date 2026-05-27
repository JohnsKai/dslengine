package com.chenk.iot.dslengine.node;

import com.chenk.iot.dslengine.context.TypedContext;
import com.chenk.iot.dslengine.event.IotEvent;

/**
 * @date 2026/05/26
 **/
public interface EventNode {
    void onEvent(IotEvent event, TypedContext context);

    default void setConfigDto(Object configDto) {
    }
}
