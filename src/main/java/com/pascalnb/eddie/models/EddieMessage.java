package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public abstract class EddieMessage<T extends EddieComponent> implements EntityProvider<MessageCreateData>,
    ComponentProvider<T> {

    private final T component;

    public EddieMessage(T component) {
        this.component = component;
    }

    @Override
    public T getComponent() {
        return component;
    }

    @Override
    public Class<MessageCreateData> getEntityType() {
        return MessageCreateData.class;
    }

}
