package com.pascalnb.eddie.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.ICustomIdInteraction;

public class JDAEventHandler extends EventHandler {

    public JDAEventHandler(String id) {
        super(id);
        addCustomIdListener(ButtonInteractionEvent.class);
        addCustomIdListener(StringSelectInteractionEvent.class);
        addCustomIdListener(ModalInteractionEvent.class);
        addEvent(SlashCommandInteractionEvent.class, SlashCommandInteractionEvent::getName);
        addGenericEvent(GenericEvent.class);
    }

    private <T extends GenericEvent & ICustomIdInteraction> void addCustomIdListener(Class<T> clazz) {
        addEvent(clazz, ICustomIdInteraction::getCustomId);
    }

}
