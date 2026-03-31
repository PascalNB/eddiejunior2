package com.pascalnb.eddie.models.menu;

import com.pascalnb.eddie.models.EddieSubcomponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public interface SettingsMenuItem {

    <E extends ActionRowChildComponent, U extends IReplyCallback & IMessageEditCallback & GenericEvent>
    EddieSubcomponent<E, U, SettingsMenu> getSubcomponent(SettingsMenu component, String id);

    String getTitle();

}
