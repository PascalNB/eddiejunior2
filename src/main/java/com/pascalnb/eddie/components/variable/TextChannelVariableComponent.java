package com.pascalnb.eddie.components.variable;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.EddieSubcomponent;
import com.pascalnb.eddie.models.dynamic.UpdatingEntitySelector;
import com.pascalnb.eddie.models.menu.SettingsMenu;
import com.pascalnb.eddie.models.menu.SettingsMenuItem;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import org.jetbrains.annotations.Nullable;

public class TextChannelVariableComponent extends VariableComponent<TextChannel> implements SettingsMenuItem {

    private final String title;

    public TextChannelVariableComponent(ComponentConfig config, String name, String title) {
        super(config, name,
            new OptionData(OptionType.CHANNEL, "channel", "channel", true)
                .setChannelTypes(ChannelType.TEXT),
            o -> o.getAsChannel().asTextChannel(),
            TextChannel::getAsMention,
            TextChannel::getId,
            config.guildManager().getGuild()::getTextChannelById
        );
        this.title = title;
    }

    public static EddieComponentFactory<TextChannelVariableComponent> factory(String name, String title) {
        return (config) -> new TextChannelVariableComponent(config, name, title);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EddieSubcomponent<EntitySelectMenu, EntitySelectInteractionEvent, SettingsMenu> getSubcomponent(
        SettingsMenu component, String id) {

        return new UpdatingEntitySelector<>(component, id) {

            @Override
            public @Nullable SettingsMenu apply(EntitySelectInteractionEvent event, InteractionHook hook) {
                try {
                    if (event.getValues().isEmpty()) {
                        setValue(null);
                    } else {
                        TextChannel channel = event.getMentions().getChannels(TextChannel.class).getFirst();
                        setValue(channel);
                    }
                } catch (CommandException e) {
                    hook.editOriginal(new MessageEditBuilder()
                        .useComponentsV2()
                        .setComponents(Container.of(
                            TextDisplay.of(e.getPrettyError())
                        ).withAccentColor(ColorUtil.RED))
                        .build()
                    ).queue();
                    return null;
                }

                return component;
            }

            @Override
            public EntitySelectMenu getEntity() {
                return hasValue()
                    ? EntitySelectMenu.create(getId(), EntitySelectMenu.SelectTarget.CHANNEL)
                      .setChannelTypes(ChannelType.TEXT)
                      .setRequired(false)
                      .setRequiredRange(0, 1)
                      .setDefaultValues(EntitySelectMenu.DefaultValue.from(getValue()))
                      .build()
                    : EntitySelectMenu.create(getId(), EntitySelectMenu.SelectTarget.CHANNEL)
                      .setChannelTypes(ChannelType.TEXT)
                      .setRequired(false)
                      .setRequiredRange(0, 1)
                      .build();
            }
        };
    }

    @Override
    public String getTitle() {
        return title;
    }

}
