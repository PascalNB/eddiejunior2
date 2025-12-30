package com.pascalnb.eddie.components.faq.edit;

import com.pascalnb.eddie.components.dynamic.DynamicComponent;
import com.pascalnb.eddie.components.dynamic.DynamicComponentFactory;
import com.pascalnb.eddie.components.dynamic.DynamicListenerChild;
import com.pascalnb.eddie.components.faq.FaqAnswerMessage;
import com.pascalnb.eddie.components.faq.FaqComponent;
import com.pascalnb.eddie.models.*;
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

public class FaqEditComponent extends DynamicComponent<FaqEditComponent> {

    private final FaqComponent component;
    private final List<FaqComponent.Question> questions;
    private final FaqComponent.Question selectedQuestion;
    private final Collection<FaqComponent.Question> changes;

    private final FaqAddButton addButton;
    private final FaqEditButton editButton;
    private final FaqDeleteButton deleteButton;
    private final FaqSubmitButton submitButton;
    private final FaqEditSelectMenu editSelectMenu;
    private final FaqAddModal addModal;
    private final FaqEditModal editModal;
    private final FaqRemoveImageButton removeImageButton;
    private final FaqCancelButton cancelButton;

    public FaqEditComponent(ComponentConfig config, FaqComponent component, DynamicListenerChild dynamic,
        Collection<FaqComponent.Question> questions) {
        this(config, component, dynamic, questions, null, List.of());
    }

    public FaqEditComponent(ComponentConfig config, FaqComponent component, DynamicListenerChild dynamic,
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

        addButton = createDynamicButton("add", FaqAddButton::new);
        editButton = createDynamicButton("edit", FaqEditButton::new);
        deleteButton = createDynamicButton("delete", FaqDeleteButton::new);
        submitButton = createDynamicButton("submit", FaqSubmitButton::new);
        editSelectMenu = createDynamicStringSelector("select", FaqEditSelectMenu::new);
        addModal = createDynamicModal("add", FaqAddModal::new);
        editModal = createDynamicModal("edit", FaqEditModal::new);
        removeImageButton = createDynamicButton("remove-image", FaqRemoveImageButton::new);
        cancelButton = createDynamicButton("cancel", FaqCancelButton::new);
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

    public FaqAddModal getAddModal() {
        return addModal;
    }

    public FaqEditModal getEditModal() {
        return editModal;
    }

    public static EddieComponentFactory<FaqEditComponent> factory(FaqComponent component, DynamicListenerChild dynamic,
        Collection<FaqComponent.Question> questions) {
        return config -> new FaqEditComponent(config, component, dynamic, questions);
    }

    public DynamicComponentFactory<FaqEditComponent> dynamicFactory(Collection<FaqComponent.Question> questions,
        @Nullable FaqComponent.Question selectedQuestion,
        Collection<FaqComponent.Question> changes) {
        return (config, dynamic) ->
            new FaqEditComponent(config, this.component, dynamic, questions, selectedQuestion, changes);
    }

    public void submit(Consumer<@Nullable Message> callback) {
        this.component.updateQuestions(questions, callback);
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
                MessageComponentTree tree = answerMessage.getMessage().getComponentTree();
                Container answerContainer = tree.getComponents().getFirst().asContainer();
                components.addAll(answerContainer.getComponents());

                components.add(
                    Separator.createDivider(Separator.Spacing.SMALL)
                );

                List<ActionRowChildComponent> buttons = new ArrayList<>();
                buttons.add(deleteButton.getButton());
                if (this.selectedQuestion.getUrl() != null) {
                    buttons.add(removeImageButton.getButton());
                }
                buttons.add(editButton.getButton());
                components.add(
                    ActionRow.of(buttons)
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
                    editSelectMenu.getMenu()
                )
            );
        }

        List<ActionRowChildComponent> bottomButtons = new ArrayList<>(List.of(
            cancelButton.getButton(),
            addButton.getButton()
        ));

        if (!changes.isEmpty()) {
            bottomButtons.add(submitButton.getButton());
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

}
