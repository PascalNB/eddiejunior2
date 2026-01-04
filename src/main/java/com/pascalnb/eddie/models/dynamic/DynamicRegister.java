package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.EventSubscriber;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.function.Function;
import java.util.function.Supplier;

public interface DynamicRegister {

    <T extends GenericEvent, R extends EventSubscriber<T>> R registerDynamic(String customId, Function<String, R> provider);

    <T extends GenericEvent, R extends EventSubscriber<T>> R registerDynamic(Supplier<R> provider);

    <T extends GenericEvent, R extends EventSubscriber<T>> R registerDynamic(R subscriber);

    String getDynamicId();

    void unmount();

}
