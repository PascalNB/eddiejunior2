package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends HandlerListener<SlashCommandInteractionEvent> {

    public void addCommand(EddieCommand<?> command) {
        this.addListener(command.getName(), command::handle);
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.handle(event.getName(), event);
    }

}