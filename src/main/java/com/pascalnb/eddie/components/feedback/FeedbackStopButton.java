package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FeedbackStopButton extends EddieButton<FeedbackComponent> {

    public FeedbackStopButton(FeedbackComponent component) {
        super(component, "feedback-stop");
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "End feedback session").withEmoji(Emoji.fromUnicode("✖️"));
    }

    @Override
    public void accept(ButtonInteractionEvent event) {
        if (!getComponent().isRunning()) {
            event.replyEmbeds(
                EmbedUtil.error("There is no feedback session running at the moment").build()
            ).setEphemeral(true).queue();
            return;
        }

        getComponent().stop();
        getComponent().getLogger().info(event.getUser(), "Stopped feedback session");
        event.replyEmbeds(EmbedUtil.ok("Feedback session stopped").build()).queue();
    }

}
