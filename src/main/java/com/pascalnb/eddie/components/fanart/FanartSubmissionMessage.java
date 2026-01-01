package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.models.EddieMessage;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FanartSubmissionMessage extends EddieMessage<FanartComponent> {

    private final Member member;
    private final String title;
    private final String description;
    private final List<Message.Attachment> attachments;

    public FanartSubmissionMessage(FanartComponent component, Member member, String title, @Nullable String description,
        List<Message.Attachment> attachments) {
        super(component);
        this.member = member;
        this.title = title;
        this.description = description;
        this.attachments = attachments;
    }

    @Override
    public MessageCreateData getEntity() {
        List<ContainerChildComponent> components = new ArrayList<>(List.of(
            Section.of(
                Thumbnail.fromUrl(member.getEffectiveAvatarUrl()),
                TextDisplay.ofFormat("""
                        ## %s
                        By %s
                        """,
                    title, member.getAsMention()
                )
            ),
            Separator.createDivider(Separator.Spacing.SMALL)
        ));

        components.add(MediaGallery.of(
            attachments.stream().map(attachment ->
                MediaGalleryItem.fromUrl(attachment.getProxy().getUrl())
            ).toList()
        ));
        components.add(Separator.createDivider(Separator.Spacing.SMALL));

        if (description != null) {
            components.add(TextDisplay.of(description));
            components.add(Separator.createDivider(Separator.Spacing.SMALL));
        }

        components.add(
            ActionRow.of(
                getComponent().getApproveButton().getEntity(),
                getComponent().getRejectButton().getEntity()
            )
        );

        return new MessageCreateBuilder().useComponentsV2()
            .setComponents(Container.of(components))
            .setAllowedMentions(List.of())
            .build();
    }

}
