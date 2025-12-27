package com.pascalnb.eddie.models;

import com.pascalnb.eddie.ComponentLogger;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collection;

public abstract class EddieComponent {

    private final ComponentConfig config;
    private final Collection<EddieCommand<?>> commands = new ArrayList<>();
    private final Collection<EddieButton<?>> buttons = new ArrayList<>();
    private final Collection<EddieModal<?>> modals = new ArrayList<>();

    public EddieComponent(ComponentConfig config) {
        this.config = config;
    }

    public final void addCommand(EddieCommand<?> command) {
        this.commands.add(command);
    }

    public final void addCommands(Collection<EddieCommand<?>> commands) {
        commands.forEach(this::addCommand);
    }

    public Collection<EddieCommand<?>> getCommands() {
        return commands;
    }

    public final void addButton(EddieButton<?> button) {
        this.buttons.add(button);
    }

    public final void addButtons(Collection<EddieButton<?>> buttons) {
        buttons.forEach(this::addButton);
    }

    public Collection<EddieButton<?>> getButtons() {
        return buttons;
    }

    public final void addModal(EddieModal<?> modal) {
        this.modals.add(modal);
    }

    public final void addModals(Collection<EddieModal<?>> modals) {
        modals.forEach(this::addModal);
    }

    public Collection<EddieModal<?>> getModals() {
        return modals;
    }

    public Guild getGuild() {
        return config.guildManager().getGuild();
    }

    public GuildManager getGuildManager() {
        return config.guildManager();
    }

    public ComponentDatabaseManager getDB() {
        return config.componentDatabaseManager();
    }

    public ComponentLogger getLogger() {
        return config.componentLogger();
    }

    public <T extends EddieComponent> T createComponent(EddieComponentFactory<T> factory) {
        return factory.apply(config);
    }

}
