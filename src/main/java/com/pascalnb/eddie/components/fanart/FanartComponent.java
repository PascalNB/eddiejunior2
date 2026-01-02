package com.pascalnb.eddie.components.fanart;

import com.pascalnb.eddie.Util;
import com.pascalnb.eddie.components.BlacklistComponent;
import com.pascalnb.eddie.components.StatusCommand;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.setting.TextChannelVariableComponent;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.ComponentConfig;
import com.pascalnb.eddie.models.EddieCommand;
import com.pascalnb.eddie.models.EddieComponent;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.util.List;

public class FanartComponent extends EddieComponent implements StatusComponent {

    private final BlacklistComponent blacklist;
    private final VariableComponent<TextChannel> channel;
    private final VariableComponent<TextChannel> reviewChannel;

    private final FanartSubmitButton submitButton = new FanartSubmitButton(this);
    private final FanartMessageModal messageModal = new FanartMessageModal(this);
    private final FanartSubmitModal submitModal = new FanartSubmitModal(this);
    private final FanartApproveButton approveButton = new FanartApproveButton(this);
    private final FanartRejectButton rejectButton = new FanartRejectButton(this);

    public FanartComponent(ComponentConfig config) {
        super(config);

        this.blacklist = createComponent(BlacklistComponent::new);
        this.channel = createComponent(TextChannelVariableComponent.factory("channel"));
        this.reviewChannel = createComponent(TextChannelVariableComponent.factory("review-channel"));

        register(
            new EddieCommand<>(this, "fanart", "Fanart", Permission.BAN_MEMBERS)
                .addSubCommands(
                    Util.spread(
                        blacklist.getCommands(),
                        new StatusCommand<>(this)
                    )
                ),
            new EddieCommand<>(this, "manage-fanart", "Manage fanart", Permission.BAN_MEMBERS,
                Permission.MANAGE_SERVER)
                .addSubCommands(
                    Util.spread(
                        channel.getCommands(),
                        reviewChannel.getCommands(),
                        new FanartMessageCommand(this)
                    )
                ),
            submitButton,
            approveButton,
            rejectButton,
            messageModal,
            submitModal
        );
    }

    public RestAction<Message> createSubmission(Member member, String title, String description,
        List<Message.Attachment> attachments) throws CommandException {
        if (!reviewChannel.hasValue()) {
            throw new CommandException("Unable to create submission");
        }
        validateMember(member);

        FanartSubmissionMessage submission = new FanartSubmissionMessage(this, member, title, description, attachments);
        MessageCreateData submissionMessage = submission.getEntity();

        return reviewChannel.getValue().sendMessage(submissionMessage);
    }

    private void validateMember(Member member) throws CommandException {
        if (blacklist.contains(member.getUser())) {
            throw new CommandException("You are banned from creating fanart posts");
        }
    }

    public RestAction<Message> forwardSubmission(MessageCreateData message) throws CommandException {
        if (!channel.hasValue()) {
            throw new CommandException("Unable to forward submission");
        }
        return channel.getValue().sendMessage(message);
    }

    public FanartSubmitButton getSubmitButton() {
        return submitButton;
    }

    public FanartMessageModal getMessageModal() {
        return messageModal;
    }

    public FanartSubmitModal getSubmitModal() {
        return submitModal;
    }

    public FanartApproveButton getApproveButton() {
        return approveButton;
    }

    public FanartRejectButton getRejectButton() {
        return rejectButton;
    }

    @Override
    public void supplyStatus(StatusCollector collector) {
        collector.addVariable("Channel", channel)
            .addVariable("Submissions channel", reviewChannel);
    }

}
