package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class FeedbackSendButtonCommand extends EddieCommand<FeedbackComponent> {

    public FeedbackSendButtonCommand(FeedbackComponent component) {
        super(component, "send-button", "Send the feedback submit button");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        event.reply(getComponent().getSubmitMenu().getMessage()).queue();
    }

}
