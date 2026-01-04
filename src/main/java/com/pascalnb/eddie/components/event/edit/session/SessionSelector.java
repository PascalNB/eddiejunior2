package com.pascalnb.eddie.components.event.edit.session;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.models.dynamic.UpdatingStringSelector;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SessionSelector extends UpdatingStringSelector<SessionComponent> {

    private final Map<String, EventComponent.Session> mappedSessions;
    private final Collection<SelectOption> options;

    public SessionSelector(SessionComponent component, String id) {
        super(component, id);

        this.mappedSessions = component.getSessions().stream()
            .collect(Collectors.toMap(
                session -> session.channel().getId(),
                Function.identity()
            ));
        this.options = mappedSessions.entrySet().stream()
            .map(entry -> SelectOption.of(entry.getValue().channel().getName(), entry.getKey()))
            .toList();
    }

    @Override
    public StringSelectMenu getEntity() {
        StringSelectMenu.Builder builder = StringSelectMenu.create(getId())
            .addOptions(options)
            .setMinValues(0)
            .setMaxValues(1);

        if (getComponent().getSelectedSession() != null) {
            String selectedValue = mappedSessions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(getComponent().getSelectedSession()))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
            builder.setDefaultValues(selectedValue);
        }

        return builder.build();
    }

    @Override
    public @Nullable SessionComponent apply(StringSelectInteractionEvent event, InteractionHook hook) {
        if (event.getValues().isEmpty()) {
            return createComponent(getComponent().factory(
                getComponent().getLink(),
                null
            ));
        }

        String value = event.getValues().getFirst();
        EventComponent.Session session = mappedSessions.get(value);

        return createComponent(getComponent().factory(
            getComponent().getLink(),
            session
        ));
    }

}
