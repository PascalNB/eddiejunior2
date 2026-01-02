package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public abstract class UpdatingStringSelector<T extends EddieComponent & UpdatingComponent<T>> extends EddieStringSelector<T>
    implements UpdatingSubcomponent<StringSelectInteractionEvent, T> {

    public UpdatingStringSelector(T component, String id) {
        super(component, id);
    }

}
