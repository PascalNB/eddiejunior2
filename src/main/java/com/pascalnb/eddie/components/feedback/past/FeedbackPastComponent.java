package com.pascalnb.eddie.components.feedback.past;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.components.feedback.FeedbackComponent;
import com.pascalnb.eddie.components.feedback.StoredSession;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieComponentFactory;
import com.pascalnb.eddie.models.EntityProvider;
import com.pascalnb.eddie.models.dynamic.DynamicComponent;
import com.pascalnb.eddie.models.dynamic.DynamicRegister;
import com.pascalnb.eddie.models.dynamic.UpdatingComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FeedbackPastComponent extends DynamicComponent<FeedbackPastComponent>
    implements UpdatingComponent<FeedbackPastComponent> {

    private final FeedbackComponent component;
    private final List<StoredSession> storedSessions;
    private final List<EntityProvider<Button>> buttons;
    private final FeedbackPastCloseButton closeButton;

    public FeedbackPastComponent(ComponentConfig config, FeedbackComponent component, DynamicRegister dynamicRegister) {
        super(config, dynamicRegister);
        this.component = component;

        this.storedSessions = getParentComponent().getPastSessions(5).reversed();
        this.buttons = IntStream.range(0, storedSessions.size())
            .boxed()
            .map(i -> {
                StoredSession session = storedSessions.get(i);
                return (EntityProvider<Button>) createDynamic(String.valueOf(i),
                    FeedbackPastRemoveButton.forSession(session));
            })
            .toList();
        this.closeButton = createDynamic("close", FeedbackPastCloseButton::new);
    }

    public FeedbackComponent getParentComponent() {
        return this.component;
    }

    @Override
    public MessageCreateData getMessage() {
        if (storedSessions.isEmpty()) {
            unmount();
            return new MessageCreateBuilder()
                .useComponentsV2()
                .setComponents(
                    Container.of(TextDisplay.of("No past feedback sessions stored"))
                        .withAccentColor(ColorUtil.RED)
                )
                .build();
        }

        List<ContainerChildComponent> sessionComponents = IntStream.range(0, storedSessions.size())
            .boxed()
            .flatMap(i -> {
                StoredSession storedSession = storedSessions.get(i);
                String winners = storedSession.winnerIds().isEmpty()
                    ? "*None*"
                    : String.join(", ", storedSession.winnerIds().stream().map(s -> "<@" + s + ">").toList());
                String submissions = (storedSession.submissionIds().isEmpty())
                    ? "*None*"
                    : String.join(", ", storedSession.submissionIds().stream().map(s -> "<@" + s + ">").toList());

                return Stream.<ContainerChildComponent>of(
                    Section.of(
                        this.buttons.get(i).getEntity(),
                        TextDisplay.ofFormat("""
                                **Session:** %s
                                **Winners:** %s
                                **Submissions:** %s
                                """, TimeFormat.DATE_TIME_LONG.format(storedSession.epoch()),
                            winners,
                            submissions
                        )
                    ),
                    Separator.createDivider(Separator.Spacing.SMALL)
                );
            }).toList();

        List<ContainerChildComponent> components = new ArrayList<>(sessionComponents);
        components.add(
            ActionRow.of(this.closeButton.getEntity())
        );

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(Container.of(components))
            .build();
    }

    @Override
    public EddieComponentFactory<FeedbackPastComponent> getCloningFactory() {
        return config -> new FeedbackPastComponent(config, this.component, getDynamic());
    }

    @Override
    public FeedbackPastComponent getComponent() {
        return this;
    }

}
