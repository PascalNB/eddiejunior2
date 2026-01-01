package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

class FaqAddButton extends EddieButton<FaqEditComponent> {

    public FaqAddButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Add FAQ").withEmoji(Emoji.fromUnicode("➕"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.replyModal(
            getComponent().createDynamic("add", FaqAddModal::new).getEntity()
        ).queue();
    }

}
