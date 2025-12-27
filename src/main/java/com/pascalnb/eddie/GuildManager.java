package com.pascalnb.eddie;

import com.pascalnb.eddie.listeners.ButtonListener;
import com.pascalnb.eddie.listeners.CommandListener;
import com.pascalnb.eddie.listeners.ModalListener;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GuildManager extends ComponentLogger {

    private final Guild guild;
    private final Collection<EddieComponent> components = new ArrayList<>();
    private final CommandListener commandListener = new CommandListener();
    private final ButtonListener buttonListener = new ButtonListener();
    private final ModalListener modalListener = new ModalListener();
    private EddieLogger logger = null;

    public GuildManager(Guild guild) {
        super(null);
        this.guild = guild;
    }

    public void addComponent(EddieComponent component) {
        component.getCommands().forEach(this.commandListener::addCommand);
        component.getButtons().forEach(this.buttonListener::addButton);
        component.getModals().forEach(this.modalListener::addModal);
        if (component instanceof EddieLogger eddieLogger) {
            // Set logger of previous components
            this.logger = eddieLogger;
            setLogger(eddieLogger);
        }
        if (this.logger != null) {
            // Set logger of next components
            component.getLogger().setLoggerProvider(this.logger);
        }
        this.components.add(component);
    }

    private void setLogger(@NotNull EddieLogger logger) {
        components.forEach(component -> component.getLogger().setLoggerProvider(logger));
    }

    public Collection<EddieComponent> getComponents() {
        return components;
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
