package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class LinkEditButton extends EddieButton<LinkEditComponent> {

    public LinkEditButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Edit").withEmoji(Emoji.fromUnicode("✏️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.replyModal(
            getComponent().createDynamic("edit", LinkEditModal::new).getEntity()
        ).queue();
    }

}
