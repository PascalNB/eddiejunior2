package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.URLUtil;
import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.StatusCommand;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.dynamic.DynamicListener;
import com.pascalnb.eddie.components.faq.edit.FaqEditComponent;
import com.pascalnb.eddie.components.setting.Variable;
import com.pascalnb.eddie.components.setting.set.VariableSet;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.RootEddieCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.tree.MessageComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.function.Consumer;

public class FaqComponent extends EddieComponent implements StatusComponent {

    private final VariableSet<Question> questions;
    private final Variable<Message> message;

    private final FaqSelector selector;
    private final FaqMessageModal messageModal = new FaqMessageModal(this);
    private final DynamicListener dynamicListener = new DynamicListener("faq");

    public FaqComponent(ComponentConfig config) {
        super(config);

        this.questions = new VariableSet<>(getDB(), "questions",
            Question::getQuestion,
            Question::toJson,
            Question::fromJson
        );

        this.message = new Variable<>(getDB(), "message",
            Message::getJumpUrl,
            m -> m == null ? null : m.getJumpUrl(),
            (url) -> {
                try {
                    return URLUtil.messageFromURL(url, getGuild());
                } catch (Exception e) {
                    return null;
                }
            }
        );

        this.selector = new FaqSelector(this);
        this.selector.update();

        addCommands(List.of(
            new RootEddieCommand<>(this, "faq", "FAQ",
                Util.spread(
                    new StatusCommand<>(this)
                ),
                Permission.BAN_MEMBERS
            ),
            new RootEddieCommand<>(this, "manage-faq", "FAQ",
                Util.spread(
                    new FaqMessageCommand(this),
                    new FaqEditCommand(this)
                ),
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER
            )
        ));

        addStringSelector(this.selector);
        addEventListener(this.dynamicListener);
        addModal(this.messageModal);
    }

    public void setMessage(Message message) throws CommandException {
        this.message.setValue(message);
    }

    public void updateQuestions(Collection<Question> newQuestions, Consumer<@Nullable Message> callback) {
        questions.replace(new HashSet<>(newQuestions));
        selector.update();
        if (!message.hasValue()) {
            callback.accept(null);
            return;
        }

        message.apply(msg -> {
            MessageComponentTree componentTree = msg.getComponentTree();
            Container container = componentTree.getComponents().getFirst().asContainer();
            ActionRow actionRow = container.getComponents().stream()
                .filter(c -> c.getType().equals(Component.Type.ACTION_ROW))
                .findFirst()
                .orElseThrow()
                .asActionRow();
            List<ContainerChildComponent> childComponents = new ArrayList<>(container.getComponents());
            int actionRowIndex = childComponents.indexOf(actionRow);
            childComponents.set(actionRowIndex, ActionRow.of(getSelector().getMenu()));
            MessageEditData editData = MessageEditBuilder.fromMessage(msg)
                .setComponents(container.withComponents(childComponents))
                .build();
            msg.editMessage(editData).queue(newMsg -> {
                getLogger().info(null, "FAQ message updated (%s)", newMsg.getJumpUrl());
                callback.accept(newMsg);
            });
        });
    }

    public FaqSelector getSelector() {
        return selector;
    }

    public FaqAnswerMessage getAnswerMessage(Question question) {
        return new FaqAnswerMessage(this, question);
    }

    public FaqEditComponent createEditMenu() {
        return createComponent(FaqEditComponent.factory(this, dynamicListener.createInstance(), getQuestions()));
    }

    public Collection<Question> getQuestions() {
        return questions.getValues();
    }

    public FaqMessageModal getMessageModal() {
        return messageModal;
    }

    @Override
    public void supplyStatus(StatusCollector collector) {
        collector.addString("Message", message.getPrettyValue());
    }

    public static final class Question {

        private final String question;
        private final String answer;
        private final @Nullable String description;
        private final @Nullable String emoji;
        private final @Nullable String url;
        private final int index;

        public Question(
            String question,
            String answer,
            @Nullable String description,
            @Nullable String emoji,
            @Nullable String url,
            @Nullable Integer index
        ) {
            this.question = question;
            this.answer = answer;
            this.description = description;
            this.emoji = emoji;
            this.url = url;
            this.index = index == null ? 0 : index;
        }

        public static Question fromJson(String json) {
            JSONObject element = new JSONObject(json);
            String q = element.getString("q");
            String a = element.getString("a");
            String e = element.optString("e", null);
            String d = element.optString("d", null);
            String u = element.optString("u", null);
            int i = element.optInt("i", 0);
            return new Question(q, a, d, e, u, i);
        }

        public String toJson() {
            JSONObject object = new JSONObject()
                .put("q", question)
                .put("a", answer)
                .put("d", description)
                .put("e", emoji)
                .put("u", url)
                .put("i", index);
            return object.toString();
        }

        public String getAnswer() {
            return answer;
        }

        public @Nullable String getDescription() {
            return description;
        }

        public @Nullable String getEmoji() {
            return emoji;
        }

        public String getQuestion() {
            return question;
        }

        public @Nullable String getUrl() {
            return url;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public int hashCode() {
            return Objects.hash(question);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            Question that = (Question) obj;
            return Objects.equals(this.question, that.question);
        }

    }

}
