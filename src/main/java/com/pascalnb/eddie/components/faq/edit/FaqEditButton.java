package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

class FaqEditButton extends EddieButton<FaqEditComponent> {

    public FaqEditButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getButton() {
        return Button.primary(getId(), "Edit").withEmoji(Emoji.fromUnicode("✏️"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        event.replyModal(getComponent().getEditModal().getModal()).queue();
    }

}
