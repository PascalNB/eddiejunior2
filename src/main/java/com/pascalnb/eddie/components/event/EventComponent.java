package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.components.RunnableComponent;
import com.pascalnb.eddie.components.event.edit.LinkEditComponent;
import com.pascalnb.eddie.components.setting.set.VariableSet;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.dynamic.DynamicSubcomponent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class EventComponent extends EddieComponent {

    private final VariableSet<Link> links;

    private final DynamicSubcomponent<EventComponent> dynamicSubcomponent;
    private final LinkSelector linkSelector;

    public EventComponent(ComponentConfig config) {
        super(config);

        links = new VariableSet<>(getDB(), "links",
            Link::toString,
            Link::toJson,
            l -> Link.fromJson(getGuild(), l)
        );

        dynamicSubcomponent = new DynamicSubcomponent<>(this, "link");
        linkSelector = new LinkSelector(this);
        linkSelector.update();

        register(
            new ScheduledEventListener(this,
                name -> handleEvent(name, true),
                name -> handleEvent(name, false)
            ),
            new EddieCommand<>(this, "manage-event-links", "Manage event links",
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER)
                .addSubCommands(
                    new EventEditCommand(this)
                ),
            new EddieCommand<>(this, "event-links", "Event links",
                Permission.BAN_MEMBERS)
                .addSubCommands(
                    new LinkViewCommand(this)
                ),
            linkSelector,
            dynamicSubcomponent
        );
    }

    private void handleEvent(String name, boolean start) {
        Collection<Link> matches = getMatchingLinks(name);
        if (matches.isEmpty()) {
            return;
        }

        Collection<RunnableComponent> components = getRunnableComponents(matches.stream()
            .flatMap(link -> link.components().stream())
            .toList()
        );

        Runnable startComponents = () -> components.forEach(component -> {
            component.start();
            getGuildManager().info("Started `%s`", component.getRunnableTitle());
        });

        Runnable stopComponents = () -> components.forEach(component -> {
            component.stop();
            getGuildManager().info("Stopped `%s`", component.getRunnableTitle());
        });

        Collection<Session> sessions = matches.stream()
            .flatMap(link -> link.sessions().stream())
            .filter(session -> getGuild().getTextChannelById(session.channel().getId()) != null)
            .toList();

        if (!sessions.isEmpty()) {
            if (start) {
                startSessions(sessions, startComponents);
            } else {
                stopSessions(sessions, stopComponents);
            }
        } else {
            if (start) {
                startComponents.run();
            } else {
                stopComponents.run();
            }
        }
    }

    private Collection<Link> getMatchingLinks(String name) {
        String lower = name.toLowerCase(Locale.ROOT);
        List<Link> matches = new ArrayList<>();
        for (Link link : this.links.getValues()) {
            if (link.keywords().stream().anyMatch(lower::contains)) {
                matches.add(link);
                break;
            }
        }
        return matches;
    }

    private Collection<RunnableComponent> getRunnableComponents(Collection<String> componentIds) {
        return getRunnableComponents().entrySet()
            .stream()
            .filter(entry -> componentIds.contains(entry.getKey()))
            .map(Map.Entry::getValue)
            .toList();
    }

    private void startSessions(Collection<Session> sessions, Runnable callback) {
        Map<TextChannel, List<MessageCreateData>> channelsAndMessages = sessions.stream()
            .collect(Collectors.groupingBy(
                Session::channel,
                Collectors.filtering(
                    session -> session.message() != null,
                    Collectors.mapping(
                        session -> new MessageCreateBuilder()
                            .useComponentsV2()
                            .setComponents(Container.of(TextDisplay.of(
                                Objects.requireNonNull(session.message()))))
                            .build(),
                        Collectors.toList()
                    )
                )
            ));

        RestAction.accumulate(
            channelsAndMessages.entrySet().stream()
                .map(entry -> {
                    try {
                        return entry.getKey().getPermissionContainer()
                            .upsertPermissionOverride(getGuild().getPublicRole())
                            .grant(Permission.VIEW_CHANNEL)
                            .flatMap(override -> {
                                if (entry.getValue().isEmpty()) {
                                    return new CompletedRestAction<>(override.getJDA(),
                                        Map.entry(override.getChannel().asTextChannel(), List.<Message>of()));
                                }
                                return RestAction.accumulate(
                                    entry.getValue().stream()
                                        .map(message -> {
                                            try {
                                                return entry.getKey().sendMessage(message)
                                                    .useComponentsV2(message.isUsingComponentsV2());
                                            } catch (InsufficientPermissionException e) {
                                                getGuildManager().error("Unable to send message in: %s",
                                                    entry.getKey().getAsMention());
                                                return new CompletedRestAction<>(override.getJDA(), (Message) null);
                                            }
                                        })
                                        .toList(),
                                    Collectors.filtering(
                                        Objects::nonNull,
                                        Collectors.collectingAndThen(
                                            Collectors.toList(),
                                            list -> Map.entry(override.getChannel().asTextChannel(), list)
                                        )
                                    )
                                );
                            });
                    } catch (InsufficientPermissionException e) {
                        getGuildManager().error("Unable to open channel: %s", entry.getKey().getAsMention());
                        return new CompletedRestAction<>(entry.getKey().getJDA(),
                            (Map.Entry<TextChannel, List<Message>>) null);
                    }
                })
                .toList(),
            Collectors.filtering(Objects::nonNull, Collectors.toList())
        ).queue(list -> {
            list.forEach(entry -> {
                getGuildManager().info("Opened channel: %s", entry.getKey().getAsMention());
                entry.getValue().forEach(msg -> getGuildManager().info(
                    "Sent message to %s: %s",
                    entry.getKey().getAsMention(), msg.getJumpUrl()));
            });
            callback.run();
        });
    }

    private void stopSessions(Collection<Session> sessions, Runnable callback) {
        RestAction.allOf(
            sessions.stream()
                .map(Session::channel)
                .distinct()
                .map(channel -> {
                    try {
                        return channel.getPermissionContainer()
                            .upsertPermissionOverride(getGuild().getPublicRole())
                            .deny(Permission.VIEW_CHANNEL)
                            .map(override -> override.getChannel().asTextChannel());
                    } catch (InsufficientPermissionException e) {
                        getGuildManager().error("Unable to close channel: %s", channel.getAsMention());
                        return new CompletedRestAction<>(channel.getJDA(), (TextChannel) null);
                    }
                })
                .toList()
        ).queue(list -> {
            list.forEach(channel -> {
                if (channel != null) {
                    getGuildManager().info("Closed channel: %s", channel.getAsMention());
                }
            });
            callback.run();
        });
    }

    public Map<String, RunnableComponent> getRunnableComponents() {
        return getGuildManager().getComponents().entrySet().stream()
            .filter(entry -> entry.getValue() instanceof RunnableComponent)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> (RunnableComponent) entry.getValue()
            ));
    }

    public LinkEditComponent createEditMenu() {
        return createComponent(LinkEditComponent.factory(this, dynamicSubcomponent.createInstance(),
            links.getValues()));
    }

    public Map<String, String> getRunnableComponentsNames() {
        return getRunnableComponents().entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().getRunnableTitle()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void updateLinks(Collection<Link> newLinks) {
        this.links.replace(newLinks);
        this.linkSelector.update();
    }

    public Collection<Link> getLinks() {
        return links.getValues();
    }

    public LinkSelector getLinkSelector() {
        return linkSelector;
    }

    public record Link(String name, Set<String> keywords, Set<String> components, Collection<Session> sessions) {

        public static Link fromJson(Guild guild, String json) {
            JSONObject obj = new JSONObject(json);

            //noinspection unchecked
            return new Link(
                obj.getString("n"),
                obj.getJSONArray("k").toList().stream()
                    .map(String.class::cast)
                    .collect(Collectors.toSet()),
                obj.getJSONArray("c").toList().stream()
                    .map(String.class::cast)
                    .collect(Collectors.toSet()),
                obj.getJSONArray("s").toList().stream()
                    .map(o -> Session.fromJson(guild, (Map<String, Object>) o))
                    .filter(Objects::nonNull)
                    .toList()
            );
        }

        public String toJson() {
            return new JSONObject()
                .put("n", name)
                .put("k", new JSONArray(keywords))
                .put("c", new JSONArray(components))
                .put("s", new JSONArray(
                    sessions.stream()
                        .map(Session::toJson)
                        .toList()
                ))
                .toString();
        }

    }

    public record Session(TextChannel channel, @Nullable String message) {

        public static @Nullable Session fromJson(Guild guild, Map<String, Object> json) {
            TextChannel channel = guild.getTextChannelById((String) json.get("c"));
            if (channel == null) {
                return null;
            }

            return new Session(
                channel,
                (String) json.get("m")
            );
        }

        public JSONObject toJson() {
            return new JSONObject()
                .put("c", channel.getId())
                .putOpt("m", message);
        }

    }

}
