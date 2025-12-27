package com.pascalnb.eddie;

import com.pascalnb.eddie.listeners.ButtonListener;
import com.pascalnb.eddie.listeners.CommandListener;
import com.pascalnb.eddie.listeners.ModalListener;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildManager {

    private final Guild guild;
    private final Collection<EddieComponent> components = new ArrayList<>();
    private final CommandListener commandListener = new CommandListener();
    private final ButtonListener buttonListener = new ButtonListener();
    private final ModalListener modalListener = new ModalListener();

    public GuildManager(Guild guild) {
        this.guild = guild;
    }

    public void addComponent(EddieComponent component) {
        this.components.add(component);
        component.getCommands().forEach(this.commandListener::addCommand);
        component.getButtons().forEach(this.buttonListener::addButton);
        component.getModals().forEach(this.modalListener::addModal);
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Collection<EventListener> getListeners() {
        return List.of(
            commandListener,
            buttonListener,
            modalListener
        );
    }

}
