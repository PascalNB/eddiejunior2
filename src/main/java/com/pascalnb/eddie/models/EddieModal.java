package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

public abstract class EddieModal<T extends EddieComponent> extends
    EddieSubcomponentBase<Modal, ModalInteractionEvent, T> {

    public EddieModal(T component, String id) {
        super(component, id);
    }

    @Override
    public Class<ModalInteractionEvent> getEventType() {
        return ModalInteractionEvent.class;
    }

    @Override
    public Class<Modal> getEntityType() {
        return Modal.class;
    }

}
