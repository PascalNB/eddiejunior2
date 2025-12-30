package com.pascalnb.eddie.models;

import com.pascalnb.eddie.ComponentLogger;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.Collection;

public interface IEddieComponent {

    default void addCommands(Collection<? extends EddieCommand<?>> commands) {
        commands.forEach(this::addCommand);
    }

    void addCommand(EddieCommand<?> command);

    Collection<EddieCommand<?>> getCommands();

    default void addButtons(Collection<? extends EddieButton<?>> buttons) {
        buttons.forEach(this::addButton);
    }

    void addButton(EddieButton<?> button);

    Collection<EddieButton<?>> getButtons();

    default void addModals(Collection<? extends EddieModal<?>> modals) {
        modals.forEach(this::addModal);
    }

    void addModal(EddieModal<?> modal);

    Collection<EddieModal<?>> getModals();

    default void addStringSelectors(Collection<? extends EddieStringSelector<?>> stringSelectors) {
        stringSelectors.forEach(this::addStringSelector);
    }

    void addStringSelector(EddieStringSelector<?> stringSelector);

    Collection<EddieStringSelector<?>> getStringSelectors();

    default void addEventListeners(Collection<? extends EventListener> listeners) {
        listeners.forEach(this::addEventListener);
    }

    void addEventListener(EventListener listener);

    Collection<EventListener> getEventListeners();

    default Guild getGuild() {
        return getGuildManager().getGuild();
    }

    default GuildManager getGuildManager() {
        return getConfig().guildManager();
    }

    default ComponentDatabaseManager getDB() {
        return getConfig().componentDatabaseManager();
    }

    default ComponentLogger getLogger() {
        return getConfig().componentLogger();
    }

    ComponentConfig getConfig();

    default <T extends EddieComponent> T createComponent(EddieComponentFactory<T> factory) {
        return factory.apply(getConfig());
    }

}
