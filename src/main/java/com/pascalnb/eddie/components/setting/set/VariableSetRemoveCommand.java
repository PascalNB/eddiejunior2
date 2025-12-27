package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Objects;

public class VariableSetRemoveCommand<T> extends EddieCommand<VariableSetComponent<T>> {

    public VariableSetRemoveCommand(VariableSetComponent<T> component) {
        super(component, "remove", "Remove from " + component.getName());
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
            getComponent().getOptionData()
        );
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        String optionName = getComponent().getOptionData().getName();
        OptionMapping optionMapping = Objects.requireNonNull(event.getOption(optionName));
        T t = getComponent().getMapper().apply(optionMapping);
        if (getComponent().removeValue(t)) {
            getComponent().getLogger().info(event.getUser(),"Removed %s from `%s`.",
                getComponent().getPrettyValue(t),
                getComponent().getName()
            );
            event.replyEmbeds(
                EmbedUtil.ok(
                    "Removed %s from `%s`",
                    getComponent().getPrettyValue(t),
                    getComponent().getName()
                ).build()
            ).queue();
        } else {
            event.replyEmbeds(
                EmbedUtil.error(
                    "`%s` does not contain %s",
                    getComponent().getName(),
                    getComponent().getPrettyValue(t)
                ).build()
            ).setEphemeral(true).queue();
        }
    }

}
