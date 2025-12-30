package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FanartSubmitButton extends EddieButton<FanartComponent> {

    public FanartSubmitButton(FanartComponent component) {
        super(component, "fanart-submit");
    }

    @Override
    public Button getButton() {
        return Button.primary(getId(), "Submit art").withEmoji(Emoji.fromUnicode("➕"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        event.replyModal(getComponent().getSubmitModal().getModal()).queue();
    }

}
