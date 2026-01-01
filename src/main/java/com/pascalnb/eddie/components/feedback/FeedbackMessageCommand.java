package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FeedbackMessageCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackMessageCommand(FeedbackComponent component) {
        super(component, "send-message", "Send the feedback submit button");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.reply(getComponent().getSubmitMenu().getEntity()).queue();
    }

}
