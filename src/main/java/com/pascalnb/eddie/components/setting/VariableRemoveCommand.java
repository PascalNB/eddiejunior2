package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class VariableRemoveCommand<T> extends EddieCommand<VariableComponent<T>> {

    public VariableRemoveCommand(VariableComponent<T> component) {
        super(component, "remove", "Remove " + component.getName());
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        try {
            getComponent().setValue(null);
            getComponent().getLogger().info(event.getUser(), "Removed `%s`", getComponent().getName());
            event.replyEmbeds(
                EmbedUtil.ok(
                    "`%s` removed.",
                    getComponent().getName()
                ).build()
            ).queue();
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
