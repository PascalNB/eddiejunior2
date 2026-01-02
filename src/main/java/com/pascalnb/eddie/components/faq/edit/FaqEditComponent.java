package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.models.dynamic.DynamicComponent;
import com.pascalnb.eddie.models.dynamic.DynamicRegister;
import com.pascalnb.eddie.components.faq.FaqAnswerMessage;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.models.*;
import com.pascalnb.eddie.models.dynamic.UpdatingComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class FaqEditComponent extends DynamicComponent<FaqEditComponent> implements UpdatingComponent<FaqEditComponent> {

    private final FaqComponent component;
    private final List<FaqComponent.Question> questions;
    private final FaqComponent.Question selectedQuestion;
    private final Collection<FaqComponent.Question> changes;

    private final FaqAddButton addButton;
    private final FaqEditButton editButton;
    private final FaqDeleteButton deleteButton;
    private final FaqSubmitButton submitButton;
    private final FaqEditSelectMenu editSelectMenu;
    private final FaqCancelButton cancelButton;

    public FaqEditComponent(ComponentConfig config, FaqComponent component, DynamicRegister dynamic,
        Collection<FaqComponent.Question> questions) {
        this(config, component, dynamic, questions, null, List.of());
    }

    public FaqEditComponent(ComponentConfig config, FaqComponent component, DynamicRegister dynamic,
        Collection<FaqComponent.Question> questions, @Nullable FaqComponent.Question selectedQuestion,
        Collection<FaqComponent.Question> changes) {
        super(config, dynamic);
        this.component = component;
        this.questions = new ArrayList<>(questions);
        if (selectedQuestion != null && !this.questions.contains(selectedQuestion)) {
            throw new IllegalArgumentException("Selected question is not in provide questions");
        }
        this.selectedQuestion = selectedQuestion;
        this.changes = changes;

        addButton = createDynamic("add", FaqAddButton::new);
        editButton = createDynamic("edit", FaqEditButton::new);
        deleteButton = createDynamic("delete", FaqDeleteButton::new);
        submitButton = createDynamic("submit", FaqSubmitButton::new);
        editSelectMenu = createDynamic("select", FaqEditSelectMenu::new);
        cancelButton = createDynamic("cancel", FaqCancelButton::new);
    }

    public List<FaqComponent.Question> getQuestions() {
        return questions;
    }

    public FaqComponent.Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public Collection<FaqComponent.Question> getChanges() {
        return changes;
    }

    public static EddieComponentFactory<FaqEditComponent> factory(FaqComponent component, DynamicRegister dynamic,
        Collection<FaqComponent.Question> questions) {
        return config -> new FaqEditComponent(config, component, dynamic, questions);
    }

    public static EddieComponentFactory<FaqEditComponent> factory(FaqComponent component, DynamicRegister dynamic,
        Collection<FaqComponent.Question> questions,
        @Nullable FaqComponent.Question selectedQuestion,
        Collection<FaqComponent.Question> changes) {
        return config -> new FaqEditComponent(config, component, dynamic, questions, selectedQuestion, changes);
    }

    public EddieComponentFactory<FaqEditComponent> factory(Collection<FaqComponent.Question> questions,
        @Nullable FaqComponent.Question selectedQuestion,
        Collection<FaqComponent.Question> changes) {
        return factory(this.component, getDynamic(), questions, selectedQuestion, changes);
    }

    public void submit(Consumer<@Nullable Message> callback) {
        this.component.updateQuestions(questions, callback);
    }

    public void unmount() {
        this.component.deregisterEditMenu(this);
    }

    @Override
    public EddieComponentFactory<FaqEditComponent> getCloningFactory() {
        return factory(this.questions, this.selectedQuestion, this.changes);
    }

    @Override
    public MessageCreateData getMessage() {
        List<ContainerChildComponent> components = new ArrayList<>();

        if (this.questions.isEmpty()) {
            components.add(
                TextDisplay.of("No FAQs yet. Use the button below to create a new FAQ.")
            );
        } else {
            // Add selecte menu
            if (this.selectedQuestion != null) {
                FaqAnswerMessage answerMessage = this.component.getAnswerMessage(this.selectedQuestion);
                MessageComponentTree tree = answerMessage.getEntity().getComponentTree();
                Container answerContainer = tree.getComponents().getFirst().asContainer();
                components.addAll(answerContainer.getComponents());

                components.add(
                    Separator.createDivider(Separator.Spacing.SMALL)
                );

                components.add(
                    ActionRow.of(
                        deleteButton.getEntity(),
                        editButton.getEntity()
                    )
                );

            } else {
                components.add(
                    TextDisplay.of("Add a new FAQ or select an existing FAQ to edit/remove.")
                );
            }
            components.add(
                Separator.createDivider(Separator.Spacing.LARGE)
            );

            components.add(
                ActionRow.of(
                    editSelectMenu.getEntity()
                )
            );
        }

        List<ActionRowChildComponent> bottomButtons = new ArrayList<>(List.of(
            cancelButton.getEntity(),
            addButton.getEntity()
        ));

        if (!changes.isEmpty()) {
            bottomButtons.add(submitButton.getEntity());
        }

        components.add(
            ActionRow.of(bottomButtons)
        );

        Container container = Container.of(components);

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(container)
            .build();
    }

    @Override
    public FaqEditComponent getComponent() {
        return this;
    }

}
