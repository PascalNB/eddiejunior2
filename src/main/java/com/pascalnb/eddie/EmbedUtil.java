package com.pascalnb.eddie;

import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;

public final class EmbedUtil {

    public static EmbedBuilder ok(String message, Object... args) {
        return ok(String.format(message, args));
    }

    public static EmbedBuilder ok(String message) {
        return ok().setDescription("✅ " + message);
    }

    public static EmbedBuilder ok() {
        return new EmbedBuilder().setColor(ColorUtil.GREEN);
    }

    public static EmbedBuilder warning(String message, Object... args) {
        return warning(String.format(message, args));
    }

    public static EmbedBuilder warning(String message) {
        return warning().setDescription("⚠️ " + message);
    }

    public static EmbedBuilder warning() {
        return new EmbedBuilder().setColor(ColorUtil.YELLOW);
    }

    public static EmbedBuilder error(CommandException e) {
        return error(e.getPrettyError());
    }

    public static EmbedBuilder error(String message) {
        return error().setDescription("❌ " + message);
    }

    public static EmbedBuilder error() {
        return new EmbedBuilder().setColor(ColorUtil.RED);
    }

    public static EmbedBuilder error(String message, Object... args) {
        return error(String.format(message, args));
    }

    public static EmbedBuilder info(String message, Object... args) {
        return info(String.format(message, args));
    }

    public static EmbedBuilder info(String message) {
        return info().setDescription(message);
    }

    public static EmbedBuilder info() {
        return new EmbedBuilder().setColor(ColorUtil.BLUE);
    }

}
