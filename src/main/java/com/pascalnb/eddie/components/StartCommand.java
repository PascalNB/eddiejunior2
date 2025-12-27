package com.pascalnb.eddie.components;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public class StartCommand<T extends EddieComponent & RunnableComponent> extends EddieCommand<T> {

    public StartCommand(T component) {
        super(component, "start", "Start the component");
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public void handle(SlashCommandInteraction event) {
        getComponent().start();
        getComponent().getLogger().info(event.getUser(), "Started `%s`", getComponent().getRunnableTitle());
        event.replyEmbeds(EmbedUtil.ok("%s started", getComponent().getRunnableTitle()).build()).queue();
    }

}
