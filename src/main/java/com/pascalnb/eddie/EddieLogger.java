package com.pascalnb.eddie;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EddieLogger {

    enum Level {
        INFO, ERROR
    }

    void log(@Nullable String componentId, @NotNull Level level, @Nullable User user, @NotNull String message);

}
