package com.pascalnb.eddie;

import com.pascalnb.dbwrapper.DatabaseAuthenticator;
import com.pascalnb.dbwrapper.DatabaseException;
import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.components.fanart.FanartComponent;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.components.feedback.FeedbackComponent;
import com.pascalnb.eddie.components.logger.LoggerComponent;
import com.pascalnb.eddie.components.modmail.ModmailComponent;
import com.pascalnb.eddie.components.ping.PingComponent;
import com.pascalnb.eddie.components.role.RoleComponent;
import com.pascalnb.eddie.database.DatabaseManager;
import com.pascalnb.eddie.listeners.GuildEventListener;
import com.pascalnb.eddie.models.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Eddie {

    public static void main(String[] args) {
        try {
            Dotenv config = Dotenv.configure().load();

            try {
                if (DatabaseManager.createDatabase()) {
                    configureDatabase(config);
                    DatabaseManager.getInstance().initialize().await();
                } else {
                    System.err.println("Database could not be created");
                }
            } catch (DatabaseException | IOException e) {
                throw new RuntimeException(e);
            }


            String token = config.get("TOKEN");
            JDA jda = buildJDA(
                token,
                new GuildEventListener(Eddie::registerGuild)
            );
            jda.getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("Starting..."));
            jda.awaitReady();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, null, false);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void configureDatabase(Dotenv config) throws DatabaseException {
        DatabaseAuthenticator.setImplementation(() -> new DatabaseAuthenticator() {
            @Override
            protected String @NotNull [] getCredentials() {
                return new String[]{
                    config.get("DB_USERNAME"),
                    config.get("DB_PASSWORD"),
                    config.get("DB_URL")
                };
            }
        });
        DatabaseAuthenticator.getInstance().authenticate();
    }

    private static GuildManager registerGuild(GuildReadyEvent guildReadyEvent) {
        Guild guild = guildReadyEvent.getGuild();
        GuildManager guildManager = new GuildManager(guild);
        Map<String, EddieComponent> components = createComponents(guildManager);
        components.forEach(guildManager::addComponent);
        registerCommands(
            guild,
            components.values().stream()
                .flatMap(c -> c.getSubcomponentsWithEntityType(CommandData.class).stream()
                    .map(EntityProvider::getEntity)
                )
                .toList()
        ).queue();
        return guildManager;
    }

    private static JDA buildJDA(String token, net.dv8tion.jda.api.hooks.EventListener... listeners) {
        return JDABuilder.createLight(token,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.SCHEDULED_EVENTS
            )
            .setMemberCachePolicy(MemberCachePolicy.NONE)
            .enableCache(
                CacheFlag.SCHEDULED_EVENTS,
                CacheFlag.ROLE_TAGS
            )
            .setRawEventsEnabled(false)
            .setEventPassthrough(false)
            .addEventListeners((Object[]) listeners)
            .build();
    }

    private static Map<String, EddieComponent> createComponents(GuildManager gm) {
        return getComponentConstructors().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().apply(
                new ComponentConfig(
                    gm,
                    DatabaseManager.getInstance().forComponent(gm.getGuild().getId(), entry.getKey()),
                    new ComponentLogger(entry.getKey())
                )
            )))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    private static RestAction<Void> registerCommands(Guild guild, Collection<? extends CommandData> commands) {
        return CommandManager.registerCommands(guild, commands).map(c -> {
            if (!c.removed().isEmpty()) System.out.println("Removed commands: " + c.removed());
            if (!c.added().isEmpty()) System.out.println("Added commands: " + c.added());
            if (!c.edited().isEmpty()) System.out.println("Edited commands: " + c.edited());
            return null;
        });
    }

    private static Map<String, EddieComponentFactory<EddieComponent>> getComponentConstructors() {
        return Map.of(
            "ping", PingComponent::new,
            "feedback", FeedbackComponent::new,
            "modmail", ModmailComponent::new,
            "log", LoggerComponent::new,
            "fanart", FanartComponent::new,
            "faq", FaqComponent::new,
            "event", EventComponent::new,
            "role", RoleComponent::new
        );
    }

}