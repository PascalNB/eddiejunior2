package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EventSubscriber;
import com.pascalnb.eddie.models.*;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class DynamicComponent<T extends DynamicComponent<T>> extends EddieComponent implements ComponentProvider<T> {

    private final DynamicRegister dynamic;

    public DynamicComponent(ComponentConfig config, DynamicRegister dynamicRegister) {
        super(config);
        this.dynamic = dynamicRegister;
    }

    public DynamicRegister getDynamic() {
        return dynamic;
    }

    public <U extends GenericEvent, R extends EventSubscriber<U>> R createDynamic(String customId, BiFunction<T, String, R> provider) {
        return this.dynamic.registerDynamic(customId, i -> provider.apply(getComponent(), i));
    }

    public <U extends GenericEvent, R extends EventSubscriber<U>> R createDynamic(Function<T, R> provider) {
        return this.dynamic.registerDynamic(provider.apply(getComponent()));
    }

    public void unmount() {
        this.dynamic.unmount();
    }

}
