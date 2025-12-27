package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class ModmailMessageCommand extends EddieCommand<ModmailComponent> {

    public ModmailMessageCommand(ModmailComponent component) {
        super(component, "send-message", "Create a new message with the modmail button");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        event.replyModal(getComponent().getMessageModal().getModal()).queue();
    }

}
