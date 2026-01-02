package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public abstract class UpdatingModal<T extends EddieComponent & UpdatingComponent<T>> extends EddieModal<T> implements
    UpdatingSubcomponent<ModalInteractionEvent, T> {

    public UpdatingModal(T component, String id) {
        super(component, id);
    }

}
