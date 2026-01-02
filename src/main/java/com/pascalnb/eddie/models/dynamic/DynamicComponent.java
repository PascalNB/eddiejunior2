package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EventSubscriber;
import com.pascalnb.eddie.models.*;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.function.BiFunction;

public abstract class DynamicComponent<T extends DynamicComponent<T>> extends EddieComponent {

    private final DynamicRegister dynamic;

    public DynamicComponent(ComponentConfig config, DynamicRegister dynamicRegister) {
        super(config);
        this.dynamic = dynamicRegister;
    }

    public T createDynamicComponent(DynamicComponentFactory<T> factory) {
        return factory.apply(getConfig(), this.dynamic);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public DynamicRegister getDynamic() {
        return dynamic;
    }

    public <V extends GenericEvent, R extends EventSubscriber<V>> R createDynamic(String customId, BiFunction<T, String, R> provider) {
        return this.dynamic.registerDynamic(customId, i -> provider.apply(self(), i));
    }

    public String getDynamicId() {
        return this.dynamic.getDynamicId();
    }

}
