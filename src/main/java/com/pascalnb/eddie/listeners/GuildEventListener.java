package com.pascalnb.eddie.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class GuildEventListener implements EventListener {

    private final Collection<Consumer<GuildReadyEvent>> guildReadyListeners = new CopyOnWriteArrayList<>();
    private final Map<Long, Collection<EventListener>> eventListeners = new ConcurrentHashMap<>();

    public void addGuildReadyListener(Consumer<GuildReadyEvent> listener) {
        this.guildReadyListeners.add(listener);
    }

    public void addEventListener(Guild guild, EventListener listener) {
        this.eventListeners.computeIfAbsent(
                guild.getIdLong(),
                k -> new CopyOnWriteArrayList<>()
            )
            .add(listener);
    }

    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildReadyEvent guildReadyEvent) {
            this.guildReadyListeners.forEach(listener -> listener.accept(guildReadyEvent));
            return;
        }

        try {
            Method method = event.getClass().getMethod("getGuild");
            Guild guild = (Guild) method.invoke(event);
            if (guild == null) {
                return;
            }

            Collection<EventListener> listeners = eventListeners.get(guild.getIdLong());
            if (listeners == null) {
                return;
            }

            listeners.forEach(listener -> listener.onEvent(event));
        } catch (NoSuchMethodException ignore) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
