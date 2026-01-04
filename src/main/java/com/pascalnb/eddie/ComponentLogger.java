package com.pascalnb.eddie;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

public class ComponentLogger {

    private final String componentId;
    private EddieLogger logger = null;

    public ComponentLogger(@Nullable String componentId) {
        this.componentId = componentId;
    }

    public void setLoggerProvider(EddieLogger logger) {
        this.logger = logger;
    }

    public void error(Throwable throwable) {
        log(EddieLogger.Level.ERROR, null, "Error: " + throwable.getMessage());
    }

    public void log(EddieLogger.Level level, @Nullable User user, String message) {
        if (logger != null) {
            logger.log(componentId, level, user, message);
        }
    }

    public void info(String message, Object... args) {
        log(EddieLogger.Level.INFO, null, message, args);
    }

    public void info(User user, String message, Object... args) {
        log(EddieLogger.Level.INFO, user, message, args);
    }

    public void log(EddieLogger.Level level, @Nullable User user, String message, Object... args) {
        log(level, user, String.format(message, args));
    }

    public void error(String message, Object... args) {
        log(EddieLogger.Level.ERROR, null, message, args);
    }

    public void error(User user, String message, Object... args) {
        log(EddieLogger.Level.ERROR, user, message, args);
    }

}
