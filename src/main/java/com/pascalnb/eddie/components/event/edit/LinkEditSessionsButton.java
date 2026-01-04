package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.components.event.edit.session.SessionComponent;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class LinkEditSessionsButton extends EddieButton<LinkEditComponent> {

    public LinkEditSessionsButton(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getEntity() {
        return Button.primary(getId(), "Edit messages").withEmoji(Emoji.fromUnicode("\uD83D\uDCAC"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        SessionComponent sessionComponent = getComponent().getDynamicSubcomponent()
            .createInstance(dynamicRegister ->
                getComponent().createComponent(config ->
                    new SessionComponent(config, getComponent(), dynamicRegister,
                        getComponent().getSelectedLink(), null)
                )
            );

        event.editMessage(MessageEditData.fromCreateData(sessionComponent.getMessage())).queue();
    }

}
