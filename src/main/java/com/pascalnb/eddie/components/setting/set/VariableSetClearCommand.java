package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class VariableSetClearCommand<T> extends EddieCommand<VariableSetComponent<T>> {

    public VariableSetClearCommand(VariableSetComponent<T> component) {
        super(component, "clear", "Clear " + component.getName());
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        if (getComponent().isEmpty()) {
            event.replyEmbeds(
                EmbedUtil.warning("`%s` is already empty", getComponent().getName()).build()
            ).queue();
        } else {
            getComponent().clear();
            event.replyEmbeds(
                EmbedUtil.ok("`%s` cleared", getComponent().getName()).build()
            ).queue();
        }
    }

}
