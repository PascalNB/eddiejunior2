package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.models.EddieSubscriberSubcomponent;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;

import java.util.function.Consumer;

public class ScheduledEventListener extends EddieSubscriberSubcomponent<GenericEvent, EventComponent> {

    private final Consumer<String> onEventNameStart;
    private final Consumer<String> onEventNameEnd;

    public ScheduledEventListener(EventComponent component, String id, Consumer<String> onEventNameStart,
        Consumer<String> onEventNameEnd) {
        super(component, id);
        this.onEventNameStart = onEventNameStart;
        this.onEventNameEnd = onEventNameEnd;
    }

    @Override
    public Class<GenericEvent> getEventType() {
        return GenericEvent.class;
    }

    @Override
    public void accept(GenericEvent genericEvent) {
        if (genericEvent instanceof ScheduledEventUpdateStatusEvent statusEvent) {
            if (statusEvent.getNewStatus().equals(statusEvent.getOldStatus())) {
                return;
            }

            GuildChannelUnion guildChannelUnion = statusEvent.getEntity().getChannel();
            if (guildChannelUnion != null && guildChannelUnion.getType().equals(ChannelType.STAGE)) {
                return; // stage channels trigger stage instances
            }

            if (ScheduledEvent.Status.SCHEDULED.equals(statusEvent.getOldStatus())
                && ScheduledEvent.Status.ACTIVE.equals(statusEvent.getNewStatus())) {
                onEventNameStart.accept(statusEvent.getScheduledEvent().getName());

            } else if (ScheduledEvent.Status.ACTIVE.equals(statusEvent.getOldStatus())
                && ScheduledEvent.Status.COMPLETED.equals(statusEvent.getNewStatus())
                || ScheduledEvent.Status.SCHEDULED.equals(statusEvent.getNewStatus())) {
                onEventNameEnd.accept(statusEvent.getScheduledEvent().getName());
            }

        } else if (genericEvent instanceof StageInstanceCreateEvent createEvent) {
            onEventNameStart.accept(createEvent.getInstance().getTopic());

        } else if (genericEvent instanceof StageInstanceDeleteEvent deleteEvent) {
            onEventNameEnd.accept(deleteEvent.getInstance().getTopic());
        }
    }

}
