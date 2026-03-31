package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class FeedbackPastCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackPastCommand(FeedbackComponent component) {
        super(component, "past-sessions", "Manage the past 5 sessions");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.deferReply().queue(hook ->
            hook.sendMessage(getComponent().getPastComponent().getMessage()).useComponentsV2().queue()
        );
    }

}
