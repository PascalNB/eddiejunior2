package com.pascalnb.eddie.components;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class StopCommand<T extends EddieComponent & RunnableComponent> extends EddieCommand<T> {

    public StopCommand(T component) {
        super(component, "stop", "Stop the component");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        getComponent().stop();
        getComponent().getLogger().info(event.getUser(), "Stopped `%s`", getComponent().getRunnableTitle());
        event.replyEmbeds(EmbedUtil.ok("%s stopped", getComponent().getRunnableTitle()).build()).queue();
    }

}
