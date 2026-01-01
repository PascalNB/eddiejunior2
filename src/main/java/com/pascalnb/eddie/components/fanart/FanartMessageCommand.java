package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class FanartMessageCommand extends EddieCommand<FanartComponent> {

    public FanartMessageCommand(FanartComponent component) {
        super(component, "send-message", "Create a new message with the fanart submit button");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.replyModal(getComponent().getMessageModal().getEntity()).queue();
    }

}
