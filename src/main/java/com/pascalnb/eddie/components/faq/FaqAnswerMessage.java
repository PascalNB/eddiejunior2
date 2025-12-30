package com.pascalnb.eddie.components.faq;

import com.pascalnb.eddie.models.EddieMenu;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

public class FaqAnswerMessage extends EddieMenu<FaqComponent> {

    private final FaqComponent.Question question;

    public FaqAnswerMessage(FaqComponent component, FaqComponent.Question question) {
        super(component);
        this.question = question;
    }

    @Override
    public MessageCreateData getMessage() {
        TextDisplay title = question.getDescription() == null
            ? TextDisplay.ofFormat("## %s", question.getQuestion())
            : TextDisplay.ofFormat("""
                    ## %s
                    *%s*
                    """,
                question.getQuestion(), question.getDescription());

        List<ContainerChildComponent> components = new ArrayList<>(List.of(
            title,
            Separator.createDivider(Separator.Spacing.SMALL),
            TextDisplay.of(question.getAnswer())
        ));

        if (question.getUrl() != null) {
            components.add(MediaGallery.of(
                MediaGalleryItem.fromUrl(question.getUrl())
            ));
        }

        return new MessageCreateBuilder()
            .useComponentsV2()
            .setComponents(Container.of(
                components
            ))
            .build();
    }

}
