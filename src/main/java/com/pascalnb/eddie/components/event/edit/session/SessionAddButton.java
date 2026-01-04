package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class SessionAddButton extends EddieButton<SessionComponent> {

    public SessionAddButton(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.success(getId(), "Add").withEmoji(Emoji.fromUnicode("➕"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.replyModal(
            getComponent().createDynamic("add", SessionAddModal::new).getEntity()
        ).queue();
    }

}
