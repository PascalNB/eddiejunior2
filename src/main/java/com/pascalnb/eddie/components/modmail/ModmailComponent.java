package com.pascalnb.eddie.components.modmail;

import com.pascalnb.eddie.Eddie;
import com.pascalnb.eddie.GuildManager;
import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.StatusCommand;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.setting.RoleVariableComponent;
import com.pascalnb.eddie.components.setting.TextChannelVariableComponent;
import com.pascalnb.eddie.components.setting.Variable;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.components.setting.set.VariableSetComponent;
import com.pascalnb.eddie.database.ComponentDatabaseManager;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.EddieComponent;
import com.pascalnb.eddie.models.RootEddieCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;

public class ModmailComponent extends EddieComponent implements StatusComponent {

    private final VariableComponent<TextChannel> channel;
    private final VariableSetComponent<User> blacklist;
    private final Variable<Integer> threadId;
    private final VariableComponent<Role> mention;

    private final ModmailSubmitButton submitButton = new ModmailSubmitButton(this);
    private final ModmailMessageModal messageModal = new ModmailMessageModal(this);
    private final ModmailSubmitModal submitModal = new ModmailSubmitModal(this);
    private final ModmailArchiveButton archiveButton = new ModmailArchiveButton(this);

    public ModmailComponent(Eddie eddie, GuildManager guildManager, ComponentDatabaseManager db) {
        super(eddie, guildManager, db);

        this.channel = createComponent(TextChannelVariableComponent.factory("channel"));
        this.blacklist = createComponent(VariableSetComponent.factory(
            "blacklist",
            new OptionData(OptionType.USER, "user", "user"),
            OptionMapping::getAsUser,
            User::getAsMention,
            ISnowflake::getId,
            (id) -> eddie.getJDA().retrieveUserById(id).onErrorMap(e -> null).complete()
        ));
        this.threadId = new Variable<>(db, "thread_id", String::valueOf, String::valueOf, Integer::valueOf, 0);
        this.mention = createComponent(RoleVariableComponent.factory("mention"));

        addCommands(
            List.of(
                new RootEddieCommand<>(this, "modmail", "Modmail",
                    Util.spread(
                        new StatusCommand<>(this),
                        blacklist.getCommands(),
                        new ModmailArchiveCommand(this)
                    ),
                    Permission.BAN_MEMBERS
                ),
                new RootEddieCommand<>(this, "modmail-manage", "Manage modmail",
                    Util.spread(
                        channel.getCommands(),
                        mention.getCommands(),
                        new ModmailMessageCommand(this)
                    ),
                    Permission.BAN_MEMBERS, Permission.MANAGE_SERVER
                )
            )
        );

        addButtons(
            List.of(
                submitButton,
                archiveButton
            )
        );
        addModals(
            List.of(
                messageModal,
                submitModal
            )
        );
    }

    public RestAction<ThreadChannel> createTicket(Member member, String title, String message,
        List<Message.Attachment> attachments)
    throws CommandException {
        if (!channel.hasValue()) {
            throw new CommandException("Unable to create ticket");
        }
        validateMember(member);

        String topic;
        synchronized (threadId) {
            threadId.setValue(threadId.getValue() + 1);
            topic = String.format("t%d-%s", this.threadId.getValue(), title);
            topic = topic.substring(0, Math.min(topic.length(), 100));
        }

        ModmailTicketMenu ticket = new ModmailTicketMenu(this, member, title, message, attachments);
        MessageCreateData ticketMessage = ticket.getMessage();

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
