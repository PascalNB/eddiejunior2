package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LinkAddButton extends EddieButton<LinkEditComponent> {

    public LinkAddButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Add link").withEmoji(Emoji.fromUnicode("➕"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.replyModal(
            getComponent().createDynamic("add", LinkAddModal::new).getEntity()
        ).queue();
    }

}
