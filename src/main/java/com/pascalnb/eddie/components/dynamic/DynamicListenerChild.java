package com.pascalnb.eddie.components.dynamic;

import com.pascalnb.eddie.models.EddieButton;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.EddieModal;
import com.pascalnb.eddie.models.EddieStringSelector;

import java.util.function.Function;

public interface DynamicListenerChild {

    <C extends EddieComponent, T extends EddieButton<C>> T createDynamicButton(String customId,
        Function<String, T> provider);

    <C extends EddieComponent, T extends EddieStringSelector<C>> T createDynamicStringSelector(String customId,
        Function<String, T> provider);

    <C extends EddieComponent, T extends EddieModal<C>> T createDynamicModal(String customId,
        Function<String, T> provider);

    String getId();

}
