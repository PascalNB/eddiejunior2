package com.pascalnb.eddie.components;

import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.components.setting.set.VariableSetComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface StatusComponent {

    void supplyStatus(StatusCollector collector);

    @SuppressWarnings("UnusedReturnValue")
    interface StatusCollector {

        @Contract("_,_ -> this")
        StatusCollector addString(String name, String value);
        @Contract("_,_ -> this")
        StatusCollector addVariable(String name, VariableComponent<?> variable);
        @Contract("_,_ -> this")
        StatusCollector addSet(String name, VariableSetComponent<?> variableSet);
        @Contract("_ -> this")
        StatusCollector addComponent(@Nullable StatusComponent supplier);
        @Contract("_,_ -> this")
        StatusCollector addBoolean(String name, boolean value);

    }

}
