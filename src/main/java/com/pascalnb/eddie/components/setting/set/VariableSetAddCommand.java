package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import java.util.Objects;

public class VariableSetAddCommand<T> extends EddieCommand<VariableSetComponent<T>> {

    public VariableSetAddCommand(VariableSetComponent<T> component) {
        super(component, "add", "Add to " + component.getName());
        addOptions(
            getComponent().getOptionData()
        );
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        try {
            String optionName = getComponent().getOptionData().getName();
            OptionMapping optionMapping = Objects.requireNonNull(event.getOption(optionName));
            T t = getComponent().getMapper().apply(optionMapping);
            getComponent().addValue(t);
            getComponent().getLogger().info(event.getUser(),"Added %s to `%s`.",
                getComponent().getPrettyValue(t),
                getComponent().getName()
            );
            event.replyEmbeds(
                EmbedUtil.ok(
                    "Added %s to `%s`",
                    getComponent().getPrettyValue(t),
                    getComponent().getName()
                ).build()
            ).queue();
        } catch (CommandException e) {
            event.replyEmbeds(EmbedUtil.error(e).build()).setEphemeral(true).queue();
        }
    }

}
