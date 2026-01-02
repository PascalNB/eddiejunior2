package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieButton;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class UpdatingButton<T extends EddieComponent & UpdatingComponent<T>> extends EddieButton<T> implements
    UpdatingSubcomponent<ButtonInteractionEvent, T> {

    public UpdatingButton(T component, String id) {
        super(component, id);
    }

}
