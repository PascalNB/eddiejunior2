package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.GenericEvent;

public interface EddieSubcomponent<E, T extends GenericEvent, R extends EddieComponent>
    extends EntityProvider<E>, ComponentProvider<R>, EventSubscriber<T> {
}
