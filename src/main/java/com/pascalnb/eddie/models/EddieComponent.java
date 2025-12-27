package com.pascalnb.eddie.models;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.Collection;

public abstract class EddieComponent {

    private final Eddie eddie;
    private final GuildManager guildManager;
    private final ComponentDatabaseManager db;
    private final Collection<EddieCommand<?>> commands = new ArrayList<>();
    private final Collection<EddieButton<?>> buttons = new ArrayList<>();
    private final Collection<EddieModal<?>> modals = new ArrayList<>();

    public EddieComponent(Eddie eddie, GuildManager guildManager, ComponentDatabaseManager db) {
        this.eddie = eddie;
        this.guildManager = guildManager;
        this.db = db;
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

    public Eddie getEddie() {
        return eddie;
    }

    public Guild getGuild() {
        return guildManager.getGuild();
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public ComponentDatabaseManager getDB() {
        return db;
    }

    public <T extends EddieComponent> T createComponent(EddieComponentFactory<T> factory) {
        return factory.createComponent(eddie, guildManager, db);
    }

}
