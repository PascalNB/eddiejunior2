package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.models.dynamic.UpdatingButton;
import com.pascalnb.eddie.components.faq.FaqComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.ArrayList;
import java.util.List;

class FaqDeleteButton extends UpdatingButton<FaqEditComponent> {

    public FaqDeleteButton(FaqEditComponent component, String id) {
        super(component, id);
    }

    @Override
    public FaqEditComponent apply(ButtonInteractionEvent event, InteractionHook hook) {
        FaqComponent.Question question = getComponent().getSelectedQuestion();

        List<FaqComponent.Question> newQuestions = new ArrayList<>(getComponent().getQuestions());
        newQuestions.remove(question);
        List<FaqComponent.Question> newChanges = new ArrayList<>(getComponent().getChanges());
        newChanges.add(question);

        return createComponent(getComponent().factory(
            newQuestions,
            null,
            newChanges
        ));
    }

    @Override
    public Button getEntity() {
        return Button.danger(getId(), "Remove").withEmoji(Emoji.fromUnicode("✖️"));
    }

}
