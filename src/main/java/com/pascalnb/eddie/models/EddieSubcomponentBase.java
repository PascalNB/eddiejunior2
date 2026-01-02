package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.GenericEvent;

public abstract class EddieSubcomponentBase<E, T extends GenericEvent, R extends EddieComponent>
    implements EddieSubcomponent<E, T, R> {

    private final R component;
    private final String id;

    public EddieSubcomponentBase(R component, String id) {
        this.component = component;
        this.id = id;
    }

    @Override
    public R getComponent() {
        return this.component;
    }

    @Override
    public String getId() {
        return this.id;
    }

}
