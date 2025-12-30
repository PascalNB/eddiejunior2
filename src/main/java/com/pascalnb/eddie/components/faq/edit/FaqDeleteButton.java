package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.components.dynamic.DynamicButton;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.ArrayList;
import java.util.List;

class FaqDeleteButton extends DynamicButton<FaqEditComponent> {

    public FaqDeleteButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public FaqEditComponent apply(ButtonInteractionEvent event) {
        FaqComponent.Question question = getComponent().getSelectedQuestion();

        List<FaqComponent.Question> newQuestions = new ArrayList<>(getComponent().getQuestions());
        newQuestions.remove(question);
        List<FaqComponent.Question> newChanges = new ArrayList<>(getComponent().getChanges());
        newChanges.add(question);

        return createComponent(getComponent().dynamicFactory(
            newQuestions,
            null,
            newChanges
        ));
    }

    @Override
    public Button getButton() {
        return Button.danger(getId(), "Remove").withEmoji(Emoji.fromUnicode("✖️"));
    }

}
