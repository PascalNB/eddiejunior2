package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.models.dynamic.UpdatingStringSelector;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.components.faq.FaqSelector;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.List;
import java.util.Map;

class FaqEditSelectMenu extends UpdatingStringSelector<FaqEditComponent> {

    private final Map<String, FaqComponent.Question> mappedQuestions;
    private final List<SelectOption> options;

    public FaqEditSelectMenu(FaqEditComponent component, String id) {
        super(component, id);

        this.mappedQuestions = FaqSelector.mapQuestions(getComponent().getQuestions());
        this.options = FaqSelector.createOptions(mappedQuestions);
    }

    @Override
    public FaqEditComponent apply(StringSelectInteractionEvent event, InteractionHook hook) {
        if (event.getValues().isEmpty()) {
            return createComponent(getComponent().factory(
               getComponent().getQuestions(),
               null,
               getComponent().getChanges()
            ));
        }

        String value = event.getValues().getFirst();
        FaqComponent.Question question = mappedQuestions.get(value);

        return createComponent(getComponent().factory(
            getComponent().getQuestions(),
            question,
            getComponent().getChanges()
        ));
    }

    @Override
    public StringSelectMenu getEntity() {
        StringSelectMenu.Builder builder = StringSelectMenu.create(getId())
            .addOptions(options)
            .setMinValues(0)
            .setMaxValues(1);

        if (getComponent().getSelectedQuestion() != null) {
            String selectedValue = mappedQuestions.entrySet().stream()
                .filter(entry -> entry.getValue().equals(getComponent().getSelectedQuestion()))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow();
            builder.setDefaultValues(selectedValue);
        }

        return builder.build();
    }



}
