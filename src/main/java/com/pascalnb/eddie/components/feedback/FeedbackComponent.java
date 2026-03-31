package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.*;
import com.pascalnb.eddie.components.*;
import com.pascalnb.eddie.components.feedback.past.FeedbackPastComponent;
import com.pascalnb.eddie.components.variable.AudioChannelVariableComponent;
import com.pascalnb.eddie.components.variable.RoleVariableComponent;
import com.pascalnb.eddie.components.variable.VariableComponent;
import com.pascalnb.eddie.components.variable.TextChannelVariableComponent;
import com.pascalnb.eddie.components.variable.set.VariableSet;
import com.pascalnb.eddie.components.variable.set.VariableSetComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import com.pascalnb.eddie.models.*;
import com.pascalnb.eddie.models.dynamic.DynamicSubcomponent;
import com.pascalnb.eddie.models.menu.SettingsMenuCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.requests.CompletedRestAction;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class FeedbackComponent extends EddieComponent implements RunnableComponent, StatusComponent {

    private final AtomicReference<FeedbackSession> session = new AtomicReference<>(null);

    private final BlacklistComponent blacklist;
    private final VariableSetComponent<String> websites;
    private final TextChannelVariableComponent submissionChannel;
    private final AudioChannelVariableComponent voiceChannel;
    private final TextChannelVariableComponent chatChannel;
    private final RoleVariableComponent winRole;
    private final VariableSet<StoredSession> storedSessions;

    private final FeedbackSubmitModal submitModal = new FeedbackSubmitModal(this);
    private final FeedbackSubmitButton submitButton = new FeedbackSubmitButton(this);
    private final FeedbackSubmitMessage submitMessage = new FeedbackSubmitMessage(this);
    private final FeedbackNextButton nextButton = new FeedbackNextButton(this);
    private final FeedbackStopButton stopButton = new FeedbackStopButton(this);
    private final FeedbackStartMessage startMenu = new FeedbackStartMessage(this);

    private final DynamicSubcomponent<FeedbackComponent> dynamicSubcomponent = new DynamicSubcomponent<>(this,
        "feedback");

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

        this.submissionChannel = createComponent(TextChannelVariableComponent.factory("submissions-channel",
            "Submissions Channel"));
        this.chatChannel = createComponent(TextChannelVariableComponent.factory("chat-channel", "Chat Channel"));
        this.winRole = createComponent(RoleVariableComponent.factory("win-role", "Winner Role"));
        this.voiceChannel = createComponent(AudioChannelVariableComponent.factory("voice-channel", "Voice Channel"));

        this.storedSessions = new VariableSet<>(config.componentDatabaseManager(), "sessions",
            s -> "`" + s + "`", StoredSession::toString, StoredSession::fromString);

        register(
            new EddieCommand<>(this, "feedback", "Feedback", Permission.BAN_MEMBERS)
                .addSubCommands(
                    Util.spread(
                        new StatusCommand<>(this),
                        new StartCommand<>(this),
                        new StopCommand<>(this),
                        blacklist.getCommands(),
                        new FeedbackButtonCommand(this),
                        new FeedbackResetCommand(this),
                        new FeedbackRemoveCommand(this),
                        new FeedbackListCommand(this)
                    )
                ),
            new EddieCommand<>(this, "manage-feedback", "Manage feedback",
                Permission.BAN_MEMBERS, Permission.MANAGE_SERVER)
                .addSubCommands(
                    Util.spread(
                        websites.getCommands(),
                        new SettingsMenuCommand<>(this, "feedback",
                            submissionChannel, chatChannel, winRole, voiceChannel),
                        new FeedbackPastCommand(this)
                    )
                ),
            this.submitButton,
            this.nextButton,
            this.stopButton,
            this.submitModal,
            this.dynamicSubcomponent
        );
    }

    public String getSubmission(Member member) throws CommandException {
        return getSessionSafe().getSubmission(member);
    }

    private FeedbackSession getSessionSafe() throws CommandException {
        FeedbackSession currentSession = session.get();
        if (currentSession == null) {
            throw new CommandException("No feedback session is currently running");
        }
        return currentSession;
    }

    public RestAction<Void> handleSubmission(Member member, String url) {
        try {
            return getSessionSafe().addSubmission(member, url);
        } catch (CommandException e) {
            return new CompletedRestAction<>(member.getJDA(), e);
        }
    }

    public RestAction<MessageEditData> handleNextSubmission(Message message) {
        try {
            return getSessionSafe().handleNextSubmission(message);
        } catch (CommandException e) {
            return new CompletedRestAction<>(message.getJDA(), e);
        }
    }

    public void resetSession() throws CommandException {
        getSessionSafe().reset();
    }

    public void resetSessionMember(Member member) throws CommandException {
        getSessionSafe().resetMember(member);
    }

    public void removeSessionSubmission(Member member, boolean resetMember) throws CommandException {
        getSessionSafe().removeSubmission(member, resetMember);
    }

    public List<Member> getQueuedMembers() throws CommandException {
        return getSessionSafe().getQueuedMembers();
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

    public FeedbackSubmitMessage getSubmitMessage() {
        return submitMessage;
    }

    public FeedbackPastComponent getPastComponent() {
        return createComponent(config -> new FeedbackPastComponent(config, this, dynamicSubcomponent.createInstance()));
    }

    @Override
    public String getRunnableTitle() {
        return "Feedback session";
    }

    @Override
    public boolean start() {
        if (session.get() != null) {
            return false;
        }
        session.set(new FeedbackSession(this, getPreviousSessions()));
        session.get().start(); // Send messages to channels
        return true;
    }

    @Override
    public boolean stop() {
        if (session.get() == null) {
            return false;
        }
        FeedbackSession currentSession = session.getAndSet(null);
        Set<String> winnerIds = currentSession.getWinnerIds();
        Set<String> submissionIds = currentSession.getSubmissionIds();
        StoredSession storedSession = new StoredSession(
            System.currentTimeMillis(),
            winnerIds,
            submissionIds
        );
        try {
            this.storedSessions.addValue(storedSession);
        } catch (CommandException e) {
            getLogger().error(e);
        }
        currentSession.reset();
        return true;
    }

    private StoredSession[] getPreviousSessions() {
        List<StoredSession> topK = getPastSessions(2);
        if (topK.isEmpty()) {
            return new StoredSession[]{StoredSession.DEFAULT, StoredSession.DEFAULT};
        } else if (topK.size() == 1) {
            return new StoredSession[]{StoredSession.DEFAULT, topK.getFirst()};
        } else {
            return topK.reversed().toArray(StoredSession[]::new);
        }
    }

    public List<StoredSession> getPastSessions(int n) {
        return this.storedSessions.getValues().stream()
            .collect(Util.topK(n, Comparator.comparing(StoredSession::epoch)));
    }

    public boolean removeSession(StoredSession storedSession) {
        return this.storedSessions.removeValue(storedSession);
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
