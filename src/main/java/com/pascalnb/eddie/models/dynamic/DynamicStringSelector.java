package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public abstract class DynamicStringSelector<T extends DynamicComponent<T>> extends EddieStringSelector<T>
    implements DynamicHandler<StringSelectInteractionEvent, T>{

    public DynamicStringSelector(T component, String id) {
        super(component, id);
    }

}
