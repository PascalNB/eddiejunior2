package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class EddieButton<T extends EddieComponent> extends
    EddieSubcomponentBase<Button, ButtonInteractionEvent, T> {

    public EddieButton(T component, String id) {
        super(component, id);
    }

    @Override
    public Class<ButtonInteractionEvent> getEventType() {
        return ButtonInteractionEvent.class;
    }

    @Override
    public Class<Button> getEntityType() {
        return Button.class;
    }

}
