package com.pascalnb.eddie.components.dynamic;

import com.pascalnb.eddie.models.*;
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

    public boolean useComponentsV2() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public <R extends EddieButton<T>> R createDynamicButton(String customId,
        BiFunction<T, String, R> provider) {
        return this.dynamic.createDynamicButton(customId, i -> provider.apply(self(), i));
    }

    public <R extends EddieStringSelector<T>> R createDynamicStringSelector(String customId,
        BiFunction<T, String, R> provider) {
        return this.dynamic.createDynamicStringSelector(customId, i -> provider.apply(self(), i));
    }

    public <R extends EddieModal<T>> R createDynamicModal(String customId,
        BiFunction<T, String, R> provider) {
        return this.dynamic.createDynamicModal(customId, i -> provider.apply(self(), i));
    }

    public String getDynamicId() {
        return this.dynamic.getId();
    }

}
