package com.pascalnb.eddie.listeners;

import com.pascalnb.eddie.GuildManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GuildEventListener implements EventListener {

    private final Function<GuildReadyEvent, GuildManager> guildManagerProvider;
    private final Map<Long, GuildManager> guildManagers = new ConcurrentHashMap<>();

    public GuildEventListener(Function<GuildReadyEvent, GuildManager> guildManagerProvider) {
        this.guildManagerProvider = guildManagerProvider;
    }

    public void onEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildReadyEvent guildReadyEvent) {
            GuildManager guildManager = guildManagerProvider.apply(guildReadyEvent);
            this.guildManagers.put(guildReadyEvent.getGuild().getIdLong(), guildManager);
            return;
        }

        try {
            Method method = event.getClass().getMethod("getGuild");
            Guild guild = (Guild) method.invoke(event);
            if (guild == null) {
                return;
            }

            GuildManager guildManager = guildManagers.get(guild.getIdLong());
            if (guildManager == null) {
                return;
            }

            try {
                guildManager.getListeners().forEach(listener -> listener.onEvent(event));
            } catch (Exception e) {
                guildManager.error(e);
                e.printStackTrace();
            }

        } catch (NoSuchMethodException ignore) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
