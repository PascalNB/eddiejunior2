package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ModmailSubmitButton extends EddieButton<ModmailComponent> {

    public ModmailSubmitButton(ModmailComponent component) {
        super(component, "modmail-submit");
    }

    @Override
    public Button getButton() {
        return Button.primary(getId(), "New ticket").withEmoji(Emoji.fromUnicode("➕"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        event.replyModal(getComponent().getSubmitModal().getModal()).queue();
    }

}
