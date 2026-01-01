package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public abstract class DynamicModal<T extends DynamicComponent<T>> extends EddieModal<T> implements DynamicHandler<ModalInteractionEvent, T> {

    public DynamicModal(T component, String id) {
        super(component, id);
    }

}
