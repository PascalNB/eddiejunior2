package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieStringSelector;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FaqSelector extends EddieStringSelector<FaqComponent> {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, FaqComponent.Question> questions = Map.of();
    private Collection<SelectOption> options = List.of();

    public FaqSelector(FaqComponent component) {
        super(component, "faq-select");
    }

    public void update() {
        lock.writeLock().lock();
        this.questions = mapQuestions(getComponent().getQuestions());
        this.options = createOptions(this.questions);
        lock.writeLock().unlock();
    }

    public static Map<String, FaqComponent.Question> mapQuestions(Collection<FaqComponent.Question> questions) {
        List<FaqComponent.Question> list = questions.stream().toList();
        return IntStream.range(0, list.size())
            .boxed()
            .collect(Collectors.toMap(
                String::valueOf,
                list::get
            ));
    }

    public static List<SelectOption> createOptions(Map<String, FaqComponent.Question> questions) {
        return questions.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.comparingInt(FaqComponent.Question::getIndex)))
            .map(entry -> {
                FaqComponent.Question question = entry.getValue();
                SelectOption option = SelectOption.of(question.getQuestion(), String.valueOf(entry.getKey()));
                if (question.getEmoji() != null) {
                    option = option.withEmoji(Emoji.fromUnicode(question.getEmoji()));
                }
                if (question.getDescription() != null) {
                    option = option.withDescription(question.getDescription());
                }
                return option;
            })
            .toList();
    }

    @Override
    public StringSelectMenu getMenu() {
        lock.readLock().lock();
        StringSelectMenu menu = StringSelectMenu.create(getId())
            .addOptions(options)
            .setMinValues(1)
            .setMaxValues(1)
            .build();
        lock.readLock().unlock();
        return menu;
    }

    @Override
    public void handle(StringSelectInteractionEvent event) {
        event.deferReply(true).queue(hook -> {
            List<String> values = event.getValues();
            String value = values.getFirst();
            lock.readLock().lock();
            FaqComponent.Question question = this.questions.get(value);
            lock.readLock().unlock();
            if (question == null) {
                hook.sendMessageEmbeds(EmbedUtil.error("Unknown question").build()).queue();
                return;
            }

            FaqAnswerMessage answerMessage = getComponent().getAnswerMessage(question);
            hook.sendMessage(answerMessage.getMessage()).queue();
        });
    }

}
