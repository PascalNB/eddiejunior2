package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.ComponentProvider;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieComponentFactory;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public interface UpdatingComponent<T extends EddieComponent & UpdatingComponent<T>> extends ComponentProvider<T> {

    MessageCreateData getMessage();

    EddieComponentFactory<T> getCloningFactory();

    default T copy() {
        return getComponent().createComponent(getCloningFactory());
    }

}
