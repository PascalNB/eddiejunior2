package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class SessionEditButton extends EddieButton<SessionComponent> {

    public SessionEditButton(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Edit").withEmoji(Emoji.fromUnicode("✏️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.replyModal(
            getComponent().createDynamic("edit", SessionEditModal::new).getEntity()
        ).queue();
    }

}
