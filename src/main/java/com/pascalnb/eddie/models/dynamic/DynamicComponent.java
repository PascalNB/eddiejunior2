package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.Handler;
import com.pascalnb.eddie.models.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.function.BiFunction;

public abstract class DynamicComponent<T extends DynamicComponent<T>> extends EddieComponent {

    private final DynamicListenerChild dynamic;

    public DynamicComponent(ComponentConfig config, DynamicListenerChild dynamicListenerChild) {
        super(config);
        this.dynamic = dynamicListenerChild;
    }

    public abstract MessageCreateData getMessage();

    public T createDynamicComponent(DynamicComponentFactory<T> factory) {
        return factory.apply(getConfig(), this.dynamic);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public abstract DynamicComponentFactory<T> getCloningFactory();

    public T copy() {
        return getCloningFactory().apply(getConfig(), dynamic);
    }

    public DynamicListenerChild getDynamic() {
        return dynamic;
    }

    public <V extends GenericEvent, R extends Handler<V>> R createDynamic(String customId, BiFunction<T, String, R> provider) {
        return this.dynamic.createDynamic(customId, i -> provider.apply(self(), i));
    }

    public String getDynamicId() {
        return this.dynamic.getDynamicId();
    }

}
