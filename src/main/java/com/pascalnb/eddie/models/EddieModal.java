package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

public abstract class EddieModal<T extends EddieComponent> implements Handler<ModalInteractionEvent> {

    private final T component;
    private final String id;

    public EddieModal(T component, String id) {
        this.component = component;
        this.id = id;
    }

    public abstract Modal getModal();

    public T getComponent() {
        return component;
    }

    public String getId() {
        return id;
    }

}
