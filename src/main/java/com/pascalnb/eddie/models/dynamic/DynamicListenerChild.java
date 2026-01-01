package com.pascalnb.eddie.models.dynamic;

import com.pascalnb.eddie.models.Handler;
import net.dv8tion.jda.api.events.GenericEvent;

import java.util.function.Function;

public interface DynamicListenerChild {

    <T extends GenericEvent, R extends Handler<T>> R createDynamic(String customId, Function<String, R> provider);

    String getDynamicId();

}
