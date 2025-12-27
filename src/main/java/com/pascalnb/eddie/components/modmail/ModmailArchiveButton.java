package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieButton;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ModmailArchiveButton extends EddieButton<ModmailComponent> {

    public ModmailArchiveButton(ModmailComponent component) {
        super(component, "modmail-archive", "Archive ticket");
    }

    @Override
    public Button getButton() {
        return Button.primary(getId(), getLabel()).withEmoji(Emoji.fromUnicode("\uD83D\uDCE5"));
    }

    @Override
    public void handle(ButtonInteractionEvent event) {
        event.replyEmbeds(EmbedUtil.ok("Archiving current thread").build())
            .queue(callback -> {
                getComponent().getLogger().info(event.getUser(), "Archived thread %s", event.getChannel().getAsMention());
                event.getChannel().asThreadChannel().getManager().setLocked(true).setArchived(true).queue();
            });
    }

}
