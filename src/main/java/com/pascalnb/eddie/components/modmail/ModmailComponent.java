package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.components.BlacklistComponent;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.StatusCommand;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.setting.RoleVariableComponent;
import com.pascalnb.eddie.components.setting.TextChannelVariableComponent;
import com.pascalnb.eddie.components.setting.Variable;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;
import java.util.Objects;

public class ModmailComponent extends EddieComponent implements StatusComponent {

    private final VariableComponent<TextChannel> channel;
    private final BlacklistComponent blacklist;
    private final Variable<Integer> threadId;
    private final VariableComponent<Role> mention;

    private final ModmailSubmitButton submitButton = new ModmailSubmitButton(this);
    private final ModmailMessageModal messageModal = new ModmailMessageModal(this);
    private final ModmailSubmitModal submitModal = new ModmailSubmitModal(this);
    private final ModmailArchiveButton archiveButton = new ModmailArchiveButton(this);

    public ModmailComponent(ComponentConfig config) {
        super(config);

        this.channel = createComponent(TextChannelVariableComponent.factory("channel"));
        this.blacklist = createComponent(BlacklistComponent::new);
        this.threadId = new Variable<>(getDB(), "thread_id", String::valueOf, String::valueOf, Integer::valueOf, 0);
        this.mention = createComponent(RoleVariableComponent.factory("mention"));

        register(
            new EddieCommand<>(this, "modmail", "Modmail", Permission.BAN_MEMBERS)
                .addSubCommands(
                    Util.spread(
                        new StatusCommand<>(this),
                        blacklist.getCommands(),
                        new ModmailArchiveCommand(this)
                    )
                ),
            new EddieCommand<>(this, "manage-modmail", "Manage modmail",
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER)
                .addSubCommands(
                    Util.spread(
                        channel.getCommands(),
                        mention.getCommands(),
                        new ModmailMessageCommand(this)
                    )
                ),
            submitButton,
            archiveButton,
            messageModal,
            submitModal
        );
    }

    public RestAction<ThreadChannel> createTicket(Member member, String title, String message,
        List<Message.Attachment> attachments) throws CommandException {
        if (!channel.hasValue()) {
            throw new CommandException("Unable to create ticket");
        }
        validateMember(member);

        String topic;
        synchronized (threadId) {
            threadId.setValue(Objects.requireNonNull(threadId.getValue()) + 1);
            topic = String.format("t%d-%s", this.threadId.getValue(), title);
            topic = topic.substring(0, Math.min(topic.length(), 100));
        }

        ModmailTicketMessage ticket = new ModmailTicketMessage(this, member, title, message, attachments);
        MessageCreateData ticketMessage = ticket.getEntity();

        return channel.getValue().createThreadChannel(topic, true).flatMap(thread ->
            thread.addThreadMember(member).flatMap(callback ->
                thread.sendMessage(ticketMessage).map(m -> thread)
            )
        );
    }

    private void validateMember(Member member) throws CommandException {
        if (blacklist.contains(member.getUser())) {
            throw new CommandException("You are banned from creating modmail tickets");
        }
    }

    public ModmailSubmitButton getSubmitButton() {
        return submitButton;
    }

    public ModmailMessageModal getMessageModal() {
        return messageModal;
    }

    public ModmailSubmitModal getSubmitModal() {
        return submitModal;
    }

    public ModmailArchiveButton getArchiveButton() {
        return archiveButton;
    }

    public VariableComponent<Role> getMention() {
        return mention;
    }

    @Override
    public void supplyStatus(StatusCollector collector) {
        collector.addVariable("Channel", channel)
            .addVariable("Mention role", mention);
    }

}
