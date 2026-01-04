package com.pascalnb.eddie.components.event;

import com.pascalnb.eddie.models.EddieMessage;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LinkViewMessage extends EddieMessage<EventComponent> {

    private final EventComponent.Link link;

    public LinkViewMessage(EventComponent component, @NotNull EventComponent.Link link) {
        super(component);
        this.link = link;
    }

    @Override
    public MessageCreateData getEntity() {
        String keywordsList = this.link.keywords().isEmpty()
            ? "*None*"
            : String.join("\n", this.link.keywords().stream().map(s -> "- `" + s + "`").toList());

        Map<String, String> componentsMap = getComponent().getRunnableComponentsNames();
        String componentsList = this.link.components().isEmpty()
            ? "*None*"
            : String.join("\n", this.link.components().stream()
                .map(componentsMap::get)
                .map(s -> "- " + s)
                .toList());

        String channelsList = this.link.sessions().isEmpty()
            ? "*None*"
            : this.link.sessions().stream()
                .map(session -> {
                    String channel = session.channel().getAsMention();
                    String message = Optional.ofNullable(session.message())
                        .map(s -> s.length() > 50 ? s.substring(0, 47) + "..." : s)
                        .map(s -> "\n    - " + String.join("\n    ", s.split("\n")))
                        .orElse("");
                    return """
                        - %s%s
                        """.formatted(channel, message);
                })
                .collect(Collectors.joining("\n"));

        return new MessageCreateBuilder()
            .useComponentsV2()
            .addComponents(Container.of(
                TextDisplay.ofFormat("""
                        **Keywords:**
                        %s
                        
                        **Components:**
                        %s
                        
                        **Channels:**
                        %s
                        """,
                    keywordsList,
                    componentsList,
                    channelsList)
            ))
            .build();
    }

}
