package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.dynamic.UpdatingModal;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LinkEditModal extends UpdatingModal<LinkEditComponent> {

    public LinkEditModal(LinkEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Modal getEntity() {
        EventComponent.Link link = getComponent().getSelectedLink();

        List<SelectOption> componentOptions = getComponent().getComponentsMap().entrySet().stream()
            .map(entry -> SelectOption.of(entry.getValue(), entry.getKey()))
            .toList();

        return Modal.create(getId(), "Edit link")
            .addComponents(
                Label.of(
                    "Name",
                    TextInput.create("name", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setRequiredRange(1, 100)
                        .setValue(link.name())
                        .build()
                ),
                Label.of(
                    "Keywords",
                    "Match keywords in event names. Separated by newlines.",
                    TextInput.create("keywords", TextInputStyle.PARAGRAPH)
                        .setRequired(true)
                        .setValue(String.join("\n", link.keywords()))
                        .build()
                ),
                Label.of(
                    "Components",
                    "Selected components are started and stopped automatically.",
                    StringSelectMenu.create("components")
                        .addOptions(
                            componentOptions
                        )
                        .setRequired(false)
                        .setMinValues(0)
                        .setDefaultValues(
                            link.components().stream()
                                // check if still exists
                                .filter(c -> componentOptions.stream().anyMatch(o -> c.equals(o.getValue())))
                                .toList()
                        )
                        .build()
                ),
                Label.of(
                    "Channels",
                    "Selected channels will be opened and closed automatically.",
                    EntitySelectMenu.create("channels", EntitySelectMenu.SelectTarget.CHANNEL)
                        .setRequired(false)
                        .setMinValues(0)
                        .setChannelTypes(ChannelType.TEXT)
                        .setDefaultValues(
                            link.sessions().stream()
                                .map(EventComponent.Session::channel)
                                // check if still exists
                                .filter(c -> c.getGuild().getTextChannelById(c.getId()) != null)
                                .map(TextChannel::getId)
                                .map(EntitySelectMenu.DefaultValue::channel)
                                .toList()
                        )
                        .build()
                )
            )
            .build();
    }

    @Override
    public @Nullable LinkEditComponent apply(ModalInteractionEvent event, InteractionHook hook) {
        String name = Objects.requireNonNull(event.getValue("name")).getAsString();
        String keywordsString = Objects.requireNonNull(event.getValue("keywords")).getAsString();

        if (keywordsString.isBlank()) {
            hook.sendMessage("Keywords cannot be empty.").queue();
            return null;
        }

        Set<String> keywords = Arrays.stream(keywordsString.split("\n"))
            .map(String::strip)
            .filter(Predicate.not(String::isBlank))
            .collect(Collectors.toSet());

        if (keywords.isEmpty()) {
            hook.sendMessage("Keywords cannot be empty.").queue();
            return null;
        }

        List<String> components = Optional.ofNullable(event.getValue("components"))
            .map(ModalMapping::getAsStringList)
            .orElse(List.of());

        List<TextChannel> channels = Optional.ofNullable(event.getValue("channels"))
            .map(m -> m.getAsMentions().getChannels(TextChannel.class))
            .orElse(List.of());

        Map<TextChannel, String> currentSessions = getComponent().getSelectedLink().sessions().stream()
            .filter(session -> session.message() != null)
            .collect(Collectors.toMap(
                EventComponent.Session::channel,
                EventComponent.Session::message
            ));

        // copy over messages
        List<EventComponent.Session> sessions = channels.stream()
            .map(channel -> new EventComponent.Session(
                channel,
                currentSessions.getOrDefault(channel, null)
            ))
            .toList();

        List<EventComponent.Link> newLinks = new ArrayList<>(getComponent().getLinks());
        newLinks.remove(getComponent().getSelectedLink());
        EventComponent.Link link = new EventComponent.Link(name, keywords, new HashSet<>(components), sessions);
        newLinks.add(link);

        return createComponent(getComponent().factory(
            newLinks,
            link,
            Util.spread(getComponent().getChanges(), link)
        ));
    }

}
