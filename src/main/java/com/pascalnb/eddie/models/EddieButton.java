package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class EddieButton<T extends IEddieComponent> implements Handler<ButtonInteractionEvent> {

    private final T component;
    private final String id;

    public EddieButton(T component, String id) {
        this.component = component;
        this.id = id;
    }

    public abstract Button getButton();

    public T getComponent() {
        return component;
    }
    public String getId() {
        return id;
    }

}
