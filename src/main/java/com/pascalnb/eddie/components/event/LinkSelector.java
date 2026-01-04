package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LinkSelector extends EddieStringSelector<EventComponent> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, EventComponent.Link> links = Map.of();
    private Collection<SelectOption> options = List.of();

    public LinkSelector(EventComponent component) {
        super(component, "event-select");
    }

    public void update() {
        lock.writeLock().lock();
        this.links = mapLinks(getComponent().getLinks());
        this.options = createOptions(this.links);
        lock.writeLock().unlock();
    }

    public static Map<String, EventComponent.Link> mapLinks(Collection<EventComponent.Link> links) {
        List<EventComponent.Link> list = links.stream().toList();
        return IntStream.range(0, list.size())
            .boxed()
            .collect(Collectors.toMap(
                String::valueOf,
                list::get
            ));
    }

    public static List<SelectOption> createOptions(Map<String, EventComponent.Link> links) {
        return links.entrySet().stream()
            .map(entry -> {
                EventComponent.Link link = entry.getValue();
                return SelectOption.of(link.name(), String.valueOf(entry.getKey()));
            })
            .toList();
    }

    @Override
    public StringSelectMenu getEntity() {
        lock.readLock().lock();
        StringSelectMenu menu = StringSelectMenu.create(getId())
            .addOptions(options)
            .setMinValues(1)
            .setMaxValues(1)
            .build();
        lock.readLock().unlock();
        return menu;
    }

    @Override
    public void accept(StringSelectInteractionEvent event) {
        event.deferEdit().queue(hook -> {
            List<String> values = event.getValues();
            String value = values.getFirst();
            lock.readLock().lock();
            EventComponent.Link link = links.get(value);
            lock.readLock().unlock();
            if (link == null) {
                hook.sendMessageEmbeds(EmbedUtil.error("Unknown link").build()).queue();
                return;
            }

            hook.editOriginal(MessageEditData.fromCreateData(
                new LinkSelectMessage(getComponent(), link).getEntity()
            )).queue();
        });
    }

}
