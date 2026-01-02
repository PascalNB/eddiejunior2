package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import java.util.Objects;

public class VariableSetCommand<T> extends EddieCommand<VariableComponent<T>> {

    public VariableSetCommand(VariableComponent<T> component) {
        super(component, "set", "Set " + component.getName());
        addOptions(
            getComponent().getOptionData()
        );
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        try {
            OptionMapping mapping = Objects.requireNonNull(event.getOption(getComponent().getOptionData().getName()));
            T value = getComponent().getMapper().apply(mapping);
            getComponent().setValue(value);
            getComponent().getLogger().info(event.getUser(),"Set `%s` to %s.", getComponent().getName(), value);
            event.replyEmbeds(
                EmbedUtil.ok("`%s` set to %s.", getComponent().getName(), value).build()
            ).queue();
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
