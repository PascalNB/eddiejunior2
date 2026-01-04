package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.models.EddieCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class LinkViewCommand extends EddieCommand<EventComponent> {

    public LinkViewCommand(EventComponent component) {
        super(component, "view", "View links");
    }

    @Override
    public void accept(SlashCommandInteractionEvent event) {
        event.reply(new LinkSelectMessage(getComponent(), null).getEntity()).queue();
    }

}
