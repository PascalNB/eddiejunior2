package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.ComponentHandler;
import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.Handler;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public interface DynamicHandler<T extends IMessageEditCallback & IReplyCallback & GenericEvent,
    R extends DynamicComponent<R>> extends ComponentHandler<T, R> {

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

    default R createComponent(DynamicComponentFactory<R> factory) {
        return getComponent().createDynamicComponent(factory);
    }

    default <V extends GenericEvent, U extends Handler<V>> U createDynamic(String customId,
        BiFunction<R, String, U> provider) {
        return getComponent().createDynamic(customId, provider);
    }

}
