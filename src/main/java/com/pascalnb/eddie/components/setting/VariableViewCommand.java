package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class VariableViewCommand<T> extends EddieCommand<VariableComponent<T>> {

    public VariableViewCommand(VariableComponent<T> component) {
        super(component, "view", "View " + component.getName());
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        String t = getComponent().getPrettyValue();
        event.replyEmbeds(
            EmbedUtil.info("`%s`: %s.".formatted(getComponent().getName(), t)).build()
        ).queue();
    }

}
