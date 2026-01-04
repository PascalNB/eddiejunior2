package com.pascalnb.eddie.models;

import net.dv8tion.jda.api.events.GenericEvent;

public abstract class EddieSubscriberSubcomponent<T extends GenericEvent, R extends EddieComponent>
    extends EddieSubcomponentBase<Void, T, R>{

    public EddieSubscriberSubcomponent(R component, String id) {
        super(component, id);
    }

    @Override
    public Class<Void> getEntityType() {
        return Void.class;
    }

    @Override
    public Void getEntity() {
        return null;
    }

}
