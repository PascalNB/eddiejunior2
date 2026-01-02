package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.*;
import com.pascalnb.eddie.components.*;
import com.pascalnb.eddie.components.setting.AudioChannelVariableComponent;
import com.pascalnb.eddie.components.setting.RoleVariableComponent;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.components.setting.TextChannelVariableComponent;
import com.pascalnb.eddie.components.setting.set.VariableSetComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class FeedbackComponent extends EddieComponent implements RunnableComponent, StatusComponent {

    private final AtomicReference<FeedbackSession> session = new AtomicReference<>(null);

    private final BlacklistComponent blacklist;
    private final VariableSetComponent<String> websites;
    private final VariableComponent<TextChannel> submissionChannel;
    private final VariableComponent<AudioChannel> voiceChannel;
    private final VariableComponent<TextChannel> chatChannel;
    private final VariableComponent<Role> winRole;

    private final FeedbackSubmitModal submitModal = new FeedbackSubmitModal(this);
    private final FeedbackSubmitButton submitButton = new FeedbackSubmitButton(this);
    private final FeedbackSubmitMessage submitMenu = new FeedbackSubmitMessage(this);
    private final FeedbackNextButton nextButton = new FeedbackNextButton(this);
    private final FeedbackStopButton stopButton = new FeedbackStopButton(this);
    private final FeedbackStartMessage startMenu = new FeedbackStartMessage(this);

    public FeedbackComponent(ComponentConfig config) {
        super(config);

        this.blacklist = createComponent(BlacklistComponent::new);

        this.websites = new VariableSetComponent<>(config, "websites",
            new OptionData(OptionType.STRING, "website", "website hostname/domain"),
            OptionMapping::getAsString, s -> "`" + s + "`",
            Function.identity(), Function.identity()
        ) {
            @Override
            public void checkPreconditions(String s) throws CommandException {
                if (!URLUtil.isDomain(s)) {
                    throw new CommandException("Invalid domain");
                }
            }
        };

        this.submissionChannel = createComponent(TextChannelVariableComponent.factory("submissions-channel"));
        this.chatChannel = createComponent(TextChannelVariableComponent.factory("chat-channel"));
        this.winRole = createComponent(RoleVariableComponent.factory("win-role"));
        this.voiceChannel = createComponent(AudioChannelVariableComponent.factory("voice-channel"));

        register(
            new EddieCommand<>(this, "feedback", "Feedback", Permission.BAN_MEMBERS)
                .addSubCommands(
                    Util.spread(
                        new StatusCommand<>(this),
                        new StartCommand<>(this),
                        new StopCommand<>(this),
                        blacklist.getCommands(),
                        new FeedbackMessageCommand(this),
                        new FeedbackResetCommand(this),
                        new FeedbackRemoveCommand(this)
                    )
                ),
            new EddieCommand<>(this, "manage-feedback", "Manage feedback",
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER)
                .addSubCommands(
                    Util.spread(
                        websites.getCommands(),
                        submissionChannel.getCommands(),
                        chatChannel.getCommands(),
                        winRole.getCommands(),
                        voiceChannel.getCommands()
                    )
                ),
            this.submitButton,
            this.nextButton,
            this.stopButton,
            this.submitModal
        );
    }

    public String getSubmission(Member member) throws CommandException {
        return getSaveSession().getSubmission(member);
    }

    private FeedbackSession getSaveSession() throws CommandException {
        FeedbackSession currentSession = session.get();
        if (currentSession == null) {
            throw new CommandException("No feedback session is currently running");
        }
        return currentSession;
    }

    public void handleSubmission(Member member, String url) throws CommandException {
        getSaveSession().addSong(member, url);  // throws CommandException on error
    }

    public void handleNextSubmission(Message message, InteractionHook hook) throws CommandException {
        getSaveSession().handleNextSubmission(message, hook);
    }

    public void resetSession() throws CommandException {
        getSaveSession().reset();
    }

    public void resetSessionMember(Member member) throws CommandException {
        getSaveSession().resetMember(member);
    }

    public void removeSessionSubmission(Member member, boolean resetMember) throws CommandException {
        getSaveSession().removeSubmission(member, resetMember);
    }

    public FeedbackNextButton getNextButton() {
        return nextButton;
    }

    public VariableSetComponent<User> getBlacklist() {
        return blacklist;
    }

    public VariableComponent<AudioChannel> getVoiceChannel() {
        return voiceChannel;
    }

    public VariableSetComponent<String> getWebsites() {
        return websites;
    }

    public VariableComponent<TextChannel> getSubmissionChannel() {
        return submissionChannel;
    }

    public VariableComponent<Role> getWinRole() {
        return winRole;
    }

    public VariableComponent<TextChannel> getChatChannel() {
        return chatChannel;
    }

    public FeedbackSubmitButton getSubmitButton() {
        return submitButton;
    }

    public FeedbackSubmitModal getSubmitModal() {
        return submitModal;
    }

    public FeedbackStopButton getStopButton() {
        return stopButton;
    }

    public FeedbackStartMessage getStartMenu() {
        return startMenu;
    }

    public FeedbackSubmitMessage getSubmitMenu() {
        return submitMenu;
    }

    @Override
    public String getRunnableTitle() {
        return "Feedback session";
    }

    @Override
    public void start() {
        session.set(new FeedbackSession(this));
        session.get().start(); // Send messages to channels
    }

    @Override
    public void stop() {
        session.getAndSet(null).reset();
    }

    @Override
    public void supplyStatus(StatusCollector collector) {
        collector.addVariable("Submissions channel", submissionChannel)
            .addVariable("Chat channel", chatChannel)
            .addVariable("Win role", winRole)
            .addVariable("Voice channel", voiceChannel)
            .addSet("Websites", websites)
            .addBoolean("Running", isRunning())
            .addComponent(session.get());
    }

    public boolean isRunning() {
        return session.get() != null;
    }

}
