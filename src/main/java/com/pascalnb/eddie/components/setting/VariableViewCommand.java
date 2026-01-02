package com.pascalnb.eddie.components.setting;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class VariableViewCommand<T> extends EddieCommand<VariableComponent<T>> {

    public VariableViewCommand(VariableComponent<T> component) {
        super(component, "view", "View " + component.getName());
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        String t = getComponent().getPrettyValue();
        event.replyEmbeds(
            EmbedUtil.info("`%s`: %s.".formatted(getComponent().getName(), t)).build()
        ).queue();
    }

}
