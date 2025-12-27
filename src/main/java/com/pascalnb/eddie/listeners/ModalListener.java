package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.models.EddieModal;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ModalListener extends HandlerListener<ModalInteractionEvent> {

    public void addModal(EddieModal<?> modal) {
        this.addListener(modal.getId(), modal);
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        this.handle(event.getCustomId(), event);
    }

}
