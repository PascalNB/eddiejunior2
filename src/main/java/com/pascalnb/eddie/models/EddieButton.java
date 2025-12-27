package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class EddieButton<T extends EddieComponent> implements Handler<ButtonInteractionEvent> {

    private final T component;
    private final String id;
    private final String label;

    public EddieButton(T component, String id, String label) {
        this.component = component;
        this.id = id;
        this.label = label;
    }

    public abstract Button getButton();

    public T getComponent() {
        return component;
    }
    public String getId() {
        return id;
    }
    public String getLabel() {
        return label;
    }

}
