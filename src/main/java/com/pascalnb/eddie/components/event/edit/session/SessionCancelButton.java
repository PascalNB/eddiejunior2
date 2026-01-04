package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class SessionCancelButton extends EddieButton<SessionComponent> {

    public SessionCancelButton(SessionComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Cancel").withEmoji(Emoji.fromUnicode("✖️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        event.editMessage(MessageEditData.fromCreateData(
            getComponent().getParentComponent().getMessage()
        )).useComponentsV2().queue(callback -> getComponent().unmount());
    }

}
