package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.ComponentProvider;
import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.EventSubscriber;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public interface UpdatingSubcomponent<T extends IMessageEditCallback & IReplyCallback & GenericEvent,
    R extends EddieComponent & UpdatingComponent<R>> extends EventSubscriber<T>, ComponentProvider<R> {

    @Override
    default void accept(T event) {
        event.deferEdit().queue(hook -> {
            R newComponent = apply(event, hook);
            if (newComponent != null) {
                MessageCreateData message = newComponent.getMessage();

                hook.editOriginal(MessageEditData.fromCreateData(message))
                    .useComponentsV2(message.isUsingComponentsV2())
                    .queue(null, failure -> {
                        getComponent().copy();
                        event.getHook().sendMessageEmbeds(
                            EmbedUtil.error("Error: %s", failure.getMessage()).build()
                        ).setEphemeral(true).queue();
                    });
            }
        });
    }

    @Nullable R apply(T t, InteractionHook hook);

    default R createComponent(EddieComponentFactory<R> factory) {
        return getComponent().createComponent(factory);
    }

}
