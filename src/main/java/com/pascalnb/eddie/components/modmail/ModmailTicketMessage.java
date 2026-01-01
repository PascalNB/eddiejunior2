package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.models.EddieMessage;
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
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.ArrayList;
import java.util.List;

public class ModmailTicketMessage extends EddieMessage<ModmailComponent> {

    private final Member member;
    private final String title;
    private final String message;
    private final List<Message.Attachment> attachments;

    public ModmailTicketMessage(ModmailComponent component, Member member, String title, String message,
        List<Message.Attachment> attachments) {
        super(component);
        this.member = member;
        this.title = title;
        this.message = message;
        this.attachments = attachments;
    }

    @Override
    public MessageCreateData getEntity() {
        List<ContainerChildComponent> components = new ArrayList<>(List.of(
            Section.of(
                Thumbnail.fromUrl(member.getEffectiveAvatarUrl()),
                TextDisplay.ofFormat("""
                        ## %s
                        %s (`%s`)
                        %s
                        """,
                    title, member.getAsMention(), member.getId(),
                    TimeFormat.DATE_TIME_LONG.format(System.currentTimeMillis())
                )
            ),
            Separator.createDivider(Separator.Spacing.SMALL),
            TextDisplay.of(message),
            Separator.createDivider(Separator.Spacing.SMALL)
        ));

        if (!attachments.isEmpty()) {
            components.add(MediaGallery.of(
                attachments.stream().map(attachment ->
                    MediaGalleryItem.fromUrl(attachment.getProxy().getUrl())
                ).toList()
            ));
            components.add(Separator.createDivider(Separator.Spacing.SMALL));
        }

        components.add(
            Section.of(
                getComponent().getArchiveButton().getEntity(),
                TextDisplay.ofFormat("""
                        Use the following button to archive this ticket.
                        Archiving this thread will close and lock it, only moderators can open it again.
                        
                        %s
                        """,
                    getComponent().getMention().hasValue()
                        ? getComponent().getMention().getValue().getAsMention()
                        : ""
                )
            )
        );

        return new MessageCreateBuilder().useComponentsV2()
            .setComponents(Container.of(components))
            .setAllowedMentions(List.of())
            .mention(
                getComponent().getMention().hasValue()
                    ? List.of(getComponent().getMention().getValue())
                    : List.of()
            )
            .build();
    }

}
