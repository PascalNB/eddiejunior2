package com.pascalnb.eddie.models;

import com.pascalnb.eddie.ComponentLogger;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class EddieComponent {

    private final ComponentConfig config;
    private final Collection<EddieSubcomponent<?, ?, ?>> subcomponents = new ArrayList<>();

    public EddieComponent(ComponentConfig config) {
        this.config = config;
    }

    public final void register(EddieSubcomponent<?, ?, ?> handler) {
        this.subcomponents.add(handler);
    }

    public final void register(Collection<? extends EddieSubcomponent<?, ?, ?>> handlers) {
        handlers.forEach(this::register);
    }

    public final void register(EddieSubcomponent<?, ?, ?>... handlers) {
        register(List.of(handlers));
    }

    public Collection<EddieSubcomponent<?, ?, ?>> getSubcomponents() {
        return subcomponents;
    }

    public Collection<EddieCommand<?>> getCommands() {
        return subcomponents.stream()
            .filter(handler -> handler instanceof EddieCommand)
            .map(handler -> (EddieCommand<?>) handler)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public <T> Collection<EddieSubcomponent<T, ?, ?>> getSubcomponentsWithEntityType(Class<T> entityType) {
        //noinspection unchecked
        return subcomponents.stream()
            .filter(handler -> entityType.isAssignableFrom(handler.getEntityType()))
            .map(handler -> (EddieSubcomponent<T, ?, ?>) handler)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public <T extends GenericEvent> Collection<EddieSubcomponent<?, T, ?>> getSubcomponentsWithEventType(Class<T> eventType) {
        //noinspection unchecked
        return subcomponents.stream()
            .filter(handler -> eventType.isAssignableFrom(handler.getType()))
            .map(handler -> (EddieSubcomponent<?, T, ?>) handler)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public ComponentConfig getConfig() {
        return config;
    }

    public Guild getGuild() {
        return getGuildManager().getGuild();
    }

    public GuildManager getGuildManager() {
        return getConfig().guildManager();
    }

    public ComponentDatabaseManager getDB() {
        return getConfig().componentDatabaseManager();
    }

    public ComponentLogger getLogger() {
        return getConfig().componentLogger();
    }

    public <T extends EddieComponent> T createComponent(EddieComponentFactory<T> factory) {
        return factory.apply(getConfig());
    }

}
