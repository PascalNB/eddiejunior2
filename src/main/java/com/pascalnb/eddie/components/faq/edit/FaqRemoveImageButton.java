package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.dynamic.DynamicButton;
import com.pascalnb.eddie.components.faq.FaqComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.List;

public class FaqRemoveImageButton extends DynamicButton<FaqEditComponent> {

    public FaqRemoveImageButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public FaqEditComponent apply(ButtonInteractionEvent event) {
        FaqComponent.Question currentQuestion = getComponent().getSelectedQuestion();

        FaqComponent.Question newQuestion = new FaqComponent.Question(
            currentQuestion.getQuestion(),
            currentQuestion.getAnswer(),
            currentQuestion.getDescription(),
            currentQuestion.getEmoji(),
            null,
            currentQuestion.getIndex()
        );

        List<FaqComponent.Question> questions = new ArrayList<>(getComponent().getQuestions());
        questions.remove(currentQuestion);
        questions.add(newQuestion);

        return createComponent(getComponent().dynamicFactory(
            questions,
            newQuestion,
            Util.spread(getComponent().getChanges(), newQuestion)
        ));

    }

    @Override
    public Button getButton() {
        return Button.danger(getId(), "Remove image").withEmoji(Emoji.fromUnicode("\uD83D\uDDBC️"));
    }

}
