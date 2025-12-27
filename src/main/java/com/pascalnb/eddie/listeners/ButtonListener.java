package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ButtonListener extends HandlerListener<ButtonInteractionEvent> {

    public void addButton(EddieButton<?> button) {
        this.addListener(button.getId(), button);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        this.handle(event.getCustomId(), event);
    }

}
