package com.pascalnb.eddie.components.setting.set;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class VariableSetShowCommand<T> extends EddieCommand<VariableSetComponent<T>> {

    public VariableSetShowCommand(VariableSetComponent<T> component) {
        super(component, "show", "Show " + component.getName());
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
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
