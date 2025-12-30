package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.dynamic.DynamicModal;
import com.pascalnb.eddie.components.faq.FaqComponent;
import net.dv8tion.jda.api.components.attachmentupload.AttachmentUpload;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FaqAddModal extends DynamicModal<FaqEditComponent> {

    public FaqAddModal(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Modal getModal() {
        return Modal.create(getId(), "Add FAQ")
            .addComponents(
                Label.of(
                    "Question",
                    TextInput.create("question", TextInputStyle.SHORT)
                        .setRequiredRange(10, 100)
                        .build()
                ),
                Label.of(
                    "Description",
                    TextInput.create("description", TextInputStyle.SHORT)
                        .setRequiredRange(0, 100)
                        .setRequired(false)
                        .build()
                ),
                Label.of(
                    "Answer",
                    TextInput.create("answer", TextInputStyle.PARAGRAPH)
                        .setRequiredRange(1, 1024)
                        .build()
                ),
                Label.of(
                    "Emoji",
                    TextInput.create("emoji", TextInputStyle.SHORT)
                        .setRequired(false)
                        .build()
                ),
                Label.of(
                    "Image",
                    AttachmentUpload.create("url")
                        .setRequired(false)
                        .setMinValues(0)
                        .setMaxValues(1)
                        .build()
                ),
                Label.of(
                    "Index",
                    "A number that affects in what order questions are displayed in the select menu.",
                    TextInput.create("index", TextInputStyle.SHORT)
                        .setRequired(false)
                        .setValue("0")
                        .build()
                )
            )
            .build();
    }

    @Override
    public @Nullable FaqEditComponent apply(ModalInteractionEvent event) {
        String question = event.getValue("question").getAsString();
        String description = event.getValue("description").getAsString();
        String answer = event.getValue("answer").getAsString();
        String emoji = event.getValue("emoji").getAsString();
        String indexString = event.getValue("index").getAsString();

        ModalMapping urlMapping = event.getValue("url");
        String url;
        if (urlMapping == null || urlMapping.getAsAttachmentList().isEmpty()) {
            url = null;
        } else {
            url = urlMapping.getAsAttachmentList().getFirst().getUrl();
        }

        if (description.isBlank()) {
            description = null;
        }
        if (emoji.isBlank()) {
            emoji = null;
        }
        Integer index = null;
        if (!indexString.isBlank()) {
            try {
                index = Integer.parseInt(indexString);
            } catch (NumberFormatException e) {
                event.replyEmbeds(EmbedUtil.error("Invalid index number: `%s`", indexString).build())
                    .setEphemeral(true)
                    .queue();
                return null;
            }
        }

        FaqComponent.Question newQuestion = new FaqComponent.Question(
            question, answer, description, emoji, url, index
        );

        List<FaqComponent.Question> newQuestions = new ArrayList<>(getComponent().getQuestions());
        newQuestions.add(newQuestion);

        return createComponent(getComponent().dynamicFactory(
            newQuestions,
            newQuestion,
            Util.spread(getComponent().getChanges(), newQuestion)
        ));
    }

}
