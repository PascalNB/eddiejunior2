package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class VariableSetShowCommand<T> extends EddieCommand<VariableSetComponent<T>> {

    public VariableSetShowCommand(VariableSetComponent<T> component) {
        super(component, "show", "Show " + component.getName());
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        if (getComponent().isEmpty()) {
            event.replyEmbeds(
                EmbedUtil.warning("`%s` is empty", getComponent().getName()).build()
            ).queue();
        } else {
            event.replyEmbeds(
                EmbedUtil.info(getComponent().getPrettyValues()).build()
            ).queue();
        }
    }

}
