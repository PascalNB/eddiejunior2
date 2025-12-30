package com.pascalnb.eddie.components.dynamic;

import com.pascalnb.eddie.models.ComponentConfig;

public interface DynamicComponentFactory<T extends DynamicComponent<T>> {

    T apply(ComponentConfig config, DynamicListenerChild dynamicListenerChild);


}
