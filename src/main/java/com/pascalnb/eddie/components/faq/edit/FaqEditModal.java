package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.models.dynamic.DynamicModal;
import com.pascalnb.eddie.components.faq.FaqComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.modals.Modal;

import java.util.ArrayList;
import java.util.List;

public class FaqEditModal extends DynamicModal<FaqEditComponent> {

    public FaqEditModal(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public Modal getEntity() {
        FaqComponent.Question question = getComponent().getSelectedQuestion();

        return Modal.create(getId(), "Add FAQ")
            .addComponents(
                Label.of(
                    "Question",
                    TextInput.create("question", TextInputStyle.SHORT)
                        .setRequiredRange(10, 100)
                        .setValue(question.getQuestion())
                        .build()
                ),
                Label.of(
                    "Description",
                    TextInput.create("description", TextInputStyle.SHORT)
                        .setRequiredRange(0, 100)
                        .setRequired(false)
                        .setValue(question.getDescription())
                        .build()
                ),
                Label.of(
                    "Answer",
                    TextInput.create("answer", TextInputStyle.PARAGRAPH)
                        .setRequiredRange(1, 1024)
                        .setValue(question.getAnswer())
                        .build()
                ),
                Label.of(
                    "Emoji",
                    TextInput.create("emoji", TextInputStyle.SHORT)
                        .setRequired(false)
                        .setValue(question.getEmoji())
                        .build()
                ),
                Label.of(
                    "Index",
                    "A number that affects in what order questions are displayed in the select menu.",
                    TextInput.create("index", TextInputStyle.SHORT)
                        .setRequired(false)
                        .setValue(String.valueOf(question.getIndex()))
                        .build()
                )
            )
            .build();
    }

    @Override
    public FaqEditComponent apply(ModalInteractionEvent event, InteractionHook hook) {
        String question = event.getValue("question").getAsString();
        String description = event.getValue("description").getAsString();
        String answer = event.getValue("answer").getAsString();
        String emoji = event.getValue("emoji").getAsString();
        String indexString = event.getValue("index").getAsString();

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
                hook.sendMessageEmbeds(EmbedUtil.error("Invalid index number: `%s`", indexString).build())
                    .setEphemeral(true)
                    .queue();
                return null;
            }
        }

        FaqComponent.Question newQuestion = new FaqComponent.Question(
            question, answer, description, emoji, index
        );

        List<FaqComponent.Question> newQuestions = new ArrayList<>(getComponent().getQuestions());
        newQuestions.remove(getComponent().getSelectedQuestion());
        newQuestions.add(newQuestion);

        return createComponent(getComponent().dynamicFactory(
            newQuestions,
            newQuestion,
            Util.spread(getComponent().getChanges(), newQuestion)
        ));
    }

}
