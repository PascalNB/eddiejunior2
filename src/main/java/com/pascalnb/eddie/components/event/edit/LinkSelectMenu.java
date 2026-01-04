package com.pascalnb.eddie.components.event.edit;

import com.pascalnb.eddie.components.event.EventComponent;
import com.pascalnb.eddie.components.event.LinkSelector;
import com.pascalnb.eddie.models.dynamic.UpdatingStringSelector;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class LinkSelectMenu extends UpdatingStringSelector<LinkEditComponent> {

    private final Map<String, EventComponent.Link> mappedLinks;
    private final List<SelectOption> options;

    public LinkSelectMenu(LinkEditComponent component, String id) {
        super(component, id);

        this.mappedLinks = LinkSelector.mapLinks(getComponent().getLinks());
        this.options = LinkSelector.createOptions(mappedLinks);
    }

    @Override
    public StringSelectMenu getEntity() {
        StringSelectMenu.Builder builder = StringSelectMenu.create(getId())
            .addOptions(options)
            .setMinValues(0)
            .setMaxValues(1);

        if (getComponent().getSelectedLink() != null) {
            String selectedValue = mappedLinks.entrySet().stream()
                .filter(entry -> entry.getValue().equals(getComponent().getSelectedLink()))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
            builder.setDefaultValues(selectedValue);
        }

        return builder.build();
    }

    @Override
    public @Nullable LinkEditComponent apply(StringSelectInteractionEvent event, InteractionHook hook) {
        if (event.getValues().isEmpty()) {
            return createComponent(getComponent().factory(
                getComponent().getLinks(),
                null,
                getComponent().getChanges()
            ));
        }

        String value = event.getValues().getFirst();
        EventComponent.Link question = mappedLinks.get(value);

        return createComponent(getComponent().factory(
            getComponent().getLinks(),
            question,
            getComponent().getChanges()
        ));
    }

}
