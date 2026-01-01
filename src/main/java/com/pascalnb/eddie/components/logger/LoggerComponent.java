package com.pascalnb.eddie.components.logger;

import com.pascalnb.eddie.*;
import com.pascalnb.eddie.components.setting.TextChannelVariableComponent;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.SimpleEddieCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoggerComponent extends EddieComponent implements EddieLogger {

    private final VariableComponent<TextChannel> channel;

    public LoggerComponent(ComponentConfig config) {
        super(config);

        this.channel = createComponent(TextChannelVariableComponent.factory("channel"));

        register(
            new SimpleEddieCommand<>(this, "manage-logger", "Logger",
                Util.spread(
                    channel.getCommands()
                ),
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER
            )
        );
    }

    @Override
    public void log(@Nullable String componentId, @NotNull Level level, @Nullable User user, @NotNull String message) {
        channel.apply(textChannel -> {
            if (!textChannel.canTalk()) {
                return;
            }
            EmbedBuilder embed = new EmbedBuilder();

            switch (level) {
                case INFO -> embed.setColor(ColorUtil.TRANSPARENT);
                case ERROR -> embed.setColor(ColorUtil.RED);
            }

            if (user == null) {
                embed.setDescription(message);
            } else {
                embed.setDescription("%s: %s".formatted(user.getAsMention(), message));
            }

            if (componentId != null) {
                if (user != null) {
                    embed.setFooter(componentId + " • " + user.getId(), user.getEffectiveAvatarUrl());
                } else {
                    embed.setFooter(componentId);
                }
            }

            textChannel.sendMessageEmbeds(embed.build()).queue();
        });
    }

}
