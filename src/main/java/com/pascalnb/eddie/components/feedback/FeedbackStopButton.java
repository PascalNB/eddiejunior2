package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FeedbackStopButton extends EddieButton<FeedbackComponent> {

    public FeedbackStopButton(FeedbackComponent component) {
        super(component, "feedback-stop", "End feedback session");
    }

    @Override
    public Button getButton() {
        return Button.danger(getId(), getLabel()).withEmoji(Emoji.fromUnicode("✖️"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        if (!getComponent().isRunning()) {
            event.replyEmbeds(
                EmbedUtil.error("There is no feedback session running at the moment").build()
            ).setEphemeral(true).queue();
            return;
        }

        getComponent().stop();
        event.replyEmbeds(EmbedUtil.ok("Feedback session stopped").build()).queue();
    }

}
