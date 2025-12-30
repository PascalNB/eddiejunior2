package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Handler;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public abstract class EddieStringSelector<T extends IEddieComponent> implements Handler<StringSelectInteractionEvent> {

    private final T component;
    private final String id;

    public EddieStringSelector(T component, String id) {
        this.component = component;
        this.id = id;
    }

    public abstract StringSelectMenu getMenu();

    public T getComponent() {
        return component;
    }

    public String getId() {
        return id;
    }

}
