package com.pascalnb.eddie.components.role;

import com.pascalnb.eddie.EmbedUtil;
import com.pascalnb.eddie.models.EddieSubscriberSubcomponent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.Objects;

public class RoleButtonListener extends EddieSubscriberSubcomponent<GenericEvent, RoleComponent> {

    public RoleButtonListener(RoleComponent component) {
        super(component, "role-listener");
    }

    @Override
    public Class<GenericEvent> getEventType() {
        return GenericEvent.class;
    }

    @Override
    public void accept(GenericEvent genericEvent) {
        if (genericEvent instanceof ButtonInteractionEvent event) {
            if (!event.getCustomId().startsWith(RoleComponent.BUTTON_ID_PREFIX)) {
                return;
            }

            String roleAction = event.getCustomId().substring(RoleComponent.BUTTON_ID_PREFIX.length());

            String[] split = roleAction.split("_", 2);
            String action = split[0];

            if (!action.equals("a") && !action.equals("r")) {
                return;
            }

            String roleId = split[1];

            Role role;
            try {
                role = getComponent().getGuild().getRoleById(roleId);
                if (role == null) {
                    return;
                }
            } catch (NumberFormatException e) {
                return;
            }

            Member member = Objects.requireNonNull(event.getMember());

            event.deferReply(true).queue(hook -> {
                try {
                    if (action.equals("a")) {
                        if (member.getUnsortedRoles().contains(role)) {
                            hook.sendMessageEmbeds(
                                EmbedUtil.error("You already have the %s role", role.getAsMention()).build()
                            ).queue();
                            return;
                        }

                        Objects.requireNonNull(event.getGuild()).addRoleToMember(member, role)
                            .queue(success ->
                                    hook.sendMessageEmbeds(
                                        EmbedUtil.ok("You received the role %s", role.getAsMention()).build()
                                    ).queue(),
                                e ->
                                    hook.sendMessageEmbeds(
                                        EmbedUtil.error(
                                            "I don't have permission to do that:%n`%s`", e.getMessage()
                                        ).build()
                                    ).queue()
                            );
                    } else {
                        if (!member.getUnsortedRoles().contains(role)) {
                            hook.sendMessageEmbeds(
                                EmbedUtil.error("You don't have the %s role", role.getAsMention()).build()
                            ).queue();
                            return;
                        }

                        Objects.requireNonNull(event.getGuild()).removeRoleFromMember(member, role)
                            .queue(success ->
                                    hook.sendMessageEmbeds(
                                        EmbedUtil.ok("The %s role has been removed", role.getAsMention()).build()
                                    ).queue(),
                                e ->
                                    hook.sendMessageEmbeds(
                                        EmbedUtil.error(
                                            "I don't have permission to do that:%n`%s`", e.getMessage()
                                        ).build()
                                    ).queue()
                            );
                    }
                } catch (InsufficientPermissionException | HierarchyException e) {
                    hook.sendMessageEmbeds(
                            EmbedUtil.error("I don't have permission to do that:%n`%s`", e.getMessage()).build()
                        )
                        .setEphemeral(true)
                        .queue();
                }
            });
        }
    }

}
