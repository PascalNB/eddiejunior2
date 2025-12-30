package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

class FaqSubmitButton extends EddieButton<FaqEditComponent> {

    public FaqSubmitButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Button getButton() {
        return Button.success(getId(), "Submit").withEmoji(Emoji.fromUnicode("✅"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        event.deferEdit().queue(hook ->
            getComponent().submit(message -> {
                Collection<FaqComponent.Question> changes = getComponent().getChanges();
                List<String> changesQuestions = changes.stream().map(FaqComponent.Question::getQuestion).toList();
                String formattedChanges = changesQuestions.stream()
                    .map("- **%s**"::formatted)
                    .collect(Collectors.joining("\n"));

                List<ContainerChildComponent> components = new ArrayList<>(List.of(
                    TextDisplay.of("## Changes saved"),
                    Separator.createDivider(Separator.Spacing.SMALL),
                    TextDisplay.ofFormat("""
                        Changed FAQs:
                        %s
                        """, formattedChanges),
                    Separator.createDivider(Separator.Spacing.SMALL)
                ));

                if (message == null) {
                    components.add(
                        TextDisplay.of(
                            "No FAQ message detected, use `/manage-faq send-message` to send the FAQ message.")
                    );
                } else {
                    components.add(
                        TextDisplay.ofFormat(
                            "FAQ message updated: %s", message.getJumpUrl()
                        )
                    );
                }

                hook.editOriginal(new MessageEditBuilder()
                    .useComponentsV2()
                    .setComponents(
                        Container.of(components).withAccentColor(ColorUtil.GREEN)
                    )
                    .build()).useComponentsV2().queue();
            })
        );
    }

}
