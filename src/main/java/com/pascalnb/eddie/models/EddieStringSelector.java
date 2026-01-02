package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public abstract class EddieStringSelector<T extends EddieComponent> extends
    EddieSubcomponentBase<StringSelectMenu, StringSelectInteractionEvent, T> {

    public EddieStringSelector(T component, String id) {
        super(component, id);
    }

    @Override
    public Class<StringSelectInteractionEvent> getType() {
        return StringSelectInteractionEvent.class;
    }

    @Override
    public Class<StringSelectMenu> getEntityType() {
        return StringSelectMenu.class;
    }

}
