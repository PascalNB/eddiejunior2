package com.pascalnb.eddie;

import com.pascalnb.dbwrapper.DatabaseAuthenticator;
import com.pascalnb.dbwrapper.DatabaseException;
import com.pascalnb.eddie.components.feedback.FeedbackComponent;
import com.pascalnb.eddie.components.modmail.ModmailComponent;
import com.pascalnb.eddie.components.ping.PingComponent;
import com.pascalnb.eddie.database.DatabaseManager;
import com.pascalnb.eddie.listeners.GuildEventListener;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class Eddie {

    private final JDA jda;
    private final DatabaseManager databaseManager;
    private final GuildEventListener guildEventListener;

    public static void main(String[] args) {
        try {
            Dotenv config = Dotenv.configure().load();

            try {
                if (DatabaseManager.createDatabase()) {
                    configureDatabase(config);
                    DatabaseManager.initialize().await();
                } else {
                    System.err.println("Database could not be created");
                }
            } catch (DatabaseException | IOException e) {
                throw new RuntimeException(e);
            }

            String token = config.get("TOKEN");
            Eddie eddie = new Eddie(token);
            eddie.getJDA().getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("Starting..."));
            eddie.getJDA().awaitReady();
            eddie.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, null, false);

        } catch (LoginException | InterruptedException e) {
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

    public JDA getJDA() {
        return jda;
    }

    public Eddie(String token) throws LoginException {
        this.databaseManager = new DatabaseManager();
        this.guildEventListener = new GuildEventListener();
        this.guildEventListener.addGuildReadyListener(event -> this.registerGuild(event.getGuild()));

        this.jda = buildJDA(
            token,
            this.guildEventListener
        );
    }

    private void registerGuild(Guild guild) {
        GuildManager guildManager = new GuildManager(guild);
        Collection<EddieComponent> components = createComponents(guildManager);
        components.forEach(guildManager::addComponent);
        guildManager.getListeners()
            .forEach(listener -> guildEventListener.addEventListener(guild, listener));
        registerCommands(
            guild,
            components.stream()
                .flatMap(c -> c.getCommands().stream())
                .toList()
        ).queue();
    }

    private JDA buildJDA(String token, EventListener... listeners) {
        return JDABuilder.createLight(token,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.SCHEDULED_EVENTS,
                GatewayIntent.GUILD_MESSAGES
            )
            .setMemberCachePolicy(MemberCachePolicy.NONE)
            .enableCache(
                CacheFlag.MEMBER_OVERRIDES,
                CacheFlag.SCHEDULED_EVENTS,
                CacheFlag.ROLE_TAGS
            )
            .setRawEventsEnabled(false)
            .setEventPassthrough(false)
            .addEventListeners((Object[]) listeners)
            .build();
    }

    private Collection<EddieComponent> createComponents(GuildManager gm) {
        return List.of(
            new PingComponent(this, gm, databaseManager.forComponent(gm.getGuild().getId(), "ping")),
            new FeedbackComponent(this, gm, databaseManager.forComponent(gm.getGuild().getId(), "feedback")),
            new ModmailComponent(this, gm, databaseManager.forComponent(gm.getGuild().getId(), "modmail"))
        );
    }

    private RestAction<Void> registerCommands(Guild guild, Collection<EddieCommand<?>> commands) {
        List<? extends CommandData> commandDataList = commands.stream()
            .map(CommandManager::getCommandData)
            .toList();

        return CommandManager.registerCommands(guild, commandDataList).map(c -> {
            if (!c.removed().isEmpty()) System.out.println("Removed commands: " + c.removed());
            if (!c.added().isEmpty()) System.out.println("Added commands: " + c.added());
            if (!c.edited().isEmpty()) System.out.println("Edited commands: " + c.edited());
            return null;
        });
    }

}