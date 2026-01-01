package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.GenericEvent;

public interface ComponentHandler<T extends GenericEvent, R extends EddieComponent> extends Handler<T> {

    R getComponent();

}
