package com.pascalnb.eddie;

import com.pascalnb.eddie.listeners.*;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class GuildManager extends ComponentLogger implements EventListener {

    private final Guild guild;
    private final Map<String, EddieComponent> components = new HashMap<>();
    private final EventHandler eventHandler;
    private EddieLogger logger = null;

    public GuildManager(Guild guild) {
        super(null);
        this.guild = guild;
        this.eventHandler = new JDAEventHandler(guild.getId());
    }

    public void addComponent(String componentId, EddieComponent component) {
        component.getSubcomponents().forEach(eventHandler::addSubscriber);
        if (component instanceof EddieLogger eddieLogger) {
            // Set logger of previous components
            this.logger = eddieLogger;
            components.forEach((__, cc) -> cc.getLogger().setLoggerProvider(logger));
            setLoggerProvider(logger);
        }
        if (this.logger != null) {
            // Set logger of next components
            component.getLogger().setLoggerProvider(this.logger);
        }
        this.components.put(componentId, component);
    }

    public Map<String, EddieComponent> getComponents() {
        return components;
    }

    public Guild getGuild() {
        return this.guild;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {
        this.eventHandler.accept(event);
    }

}
