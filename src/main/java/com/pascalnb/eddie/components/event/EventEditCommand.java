package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class EventEditCommand extends EddieCommand<EventComponent> {

    public EventEditCommand(EventComponent component) {
        super(component, "edit", "Edit event links");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.reply(getComponent().createEditMenu().getMessage()).queue();
    }

}
