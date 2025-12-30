package com.pascalnb.eddie;

import com.pascalnb.eddie.listeners.ButtonListener;
import com.pascalnb.eddie.listeners.CommandListener;
import com.pascalnb.eddie.listeners.ModalListener;
import com.pascalnb.eddie.listeners.StringSelectListener;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;
import java.util.Collection;

public class GuildManager extends ComponentLogger {

    private final Guild guild;
    private final Collection<EddieComponent> components = new ArrayList<>();
    private final CommandListener commandListener = new CommandListener();
    private final ButtonListener buttonListener = new ButtonListener();
    private final ModalListener modalListener = new ModalListener();
    private final StringSelectListener stringSelectListener = new StringSelectListener();
    private EddieLogger logger = null;

    public GuildManager(Guild guild) {
        super(null);
        this.guild = guild;
    }

    public void addComponent(EddieComponent component) {
        component.getCommands().forEach(this.commandListener::addCommand);
        component.getButtons().forEach(this.buttonListener::addButton);
        component.getModals().forEach(this.modalListener::addModal);
        component.getStringSelectors().forEach(this.stringSelectListener::addStringSelector);
        if (component instanceof EddieLogger eddieLogger) {
            // Set logger of previous components
            this.logger = eddieLogger;
            components.forEach(cc -> cc.getLogger().setLoggerProvider(logger));
            setLoggerProvider(logger);
        }
        if (this.logger != null) {
            // Set logger of next components
            component.getLogger().setLoggerProvider(this.logger);
        }
        this.components.add(component);
    }

    public Collection<EddieComponent> getComponents() {
        return components;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public Collection<EventListener> getListeners() {
        return Util.spread(
            commandListener,
            buttonListener,
            modalListener,
            stringSelectListener,
            components.stream().flatMap(c -> c.getEventListeners().stream()).toList()
        );
    }

}
