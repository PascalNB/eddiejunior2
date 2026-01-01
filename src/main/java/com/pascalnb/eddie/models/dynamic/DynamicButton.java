package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class DynamicButton<T extends DynamicComponent<T>> extends EddieButton<T> implements
    DynamicHandler<ButtonInteractionEvent, T> {

    public DynamicButton(T component, String id) {
        super(component, id);
    }

}
