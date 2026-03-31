package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.URLUtil;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.variable.VariableComponent;
import com.pascalnb.eddie.components.variable.set.VariableSetComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class FeedbackSession implements StatusComponent {

    public static final int BASELINE = 4;
    // MISS=0, LOSE=1, WIN=2
    public static final int[][] MULTIPLIERS = new int[][]{
        new int[]{4, 6, 2},
        new int[]{6, 8, 2},
        new int[]{4, 4, 1}
    };

    private final FeedbackComponent component;
    private final Set<Member> members = new HashSet<>();
    private final List<Submission> submissions = new ArrayList<>();
    private final Set<String> winnerIds = new HashSet<>();
    private final StoredSession[] previousSessions;
    private int submissionCount = 0;
    private Member currentWinner = null;

    public FeedbackSession(FeedbackComponent component, StoredSession[] previousSessions) {
        this.component = component;
        this.previousSessions = previousSessions;
    }

    /**
     * Starts the feedback session by sending the initial messages to the appropriate channels.
     */
    public synchronized void start() {
        component.getChatChannel().accept(channel ->
            channel.sendMessage(
                component.getSubmitMessage().getEntity()
            ).queue()
        );

        component.getSubmissionChannel().accept(channel ->
            channel.sendMessage(
                component.getStartMenu().getEntity()
            ).queue()
        );
    }

    /**
     * Resets the state of the feedback session to its initial configuration.
     * <p>
     * Clears the list of session members, submissions, and resets the submission
     * count to zero. If the session's associated component holds a "win role,"
     * it transfers this role from the current winner to null (effectively
     * removing it). Finally, the `currentWinner` field is set to null.
     */
    public synchronized void reset() {
        this.members.clear();
        this.submissions.clear();
        this.submissionCount = 0;
        component.getWinRole().accept(role ->
            transferWinRole(role, this.currentWinner, null)
        );
        this.currentWinner = null;
    }

    /**
     * Transfers the "win role" to a new guild member. If a member currently holds the win role,
     * it will be removed before assigning the role to the new member.
     *
     * @param newWinner The guild member to whom the win role will be assigned.
     */
    private void transferWinRole(Role winRole, @Nullable Member currentWinner, @Nullable Member newWinner) {
        if (currentWinner != null) {
            // remove old winner role
            try {
                component.getGuild().removeRoleFromMember(currentWinner, winRole).onErrorMap(e -> null).queue();
            } catch (Exception ignore) {
            }
        }
        if (newWinner != null) {
            try {
                component.getGuild().addRoleToMember(newWinner, winRole).onErrorMap(e -> null).queue();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Removes a member's song submission from the feedback session. If the member
     * has not submitted any song, this method will throw a {@code CommandException}.
     * Optionally, it can also reset the member's participation in the session.
     *
     * @param member      The {@code Member} whose submission is to be removed.
     *                    This represents the guild member associated with the submission.
     * @param resetMember A boolean flag indicating whether to reset the member's participation
     *                    after their submission is removed. If {@code true}, the member's
     *                    session participation state will be reset.
     * @throws CommandException If the member has not submitted a song or any other error
     *                          occurs during the removal process.
     */
    public synchronized void removeSubmission(Member member, boolean resetMember) throws CommandException {
        boolean submissionRemoved = submissions.removeIf(submission -> submission.member().equals(member));
        if (!submissionRemoved) {
            throw new CommandException("User did not submit a song");
        }
        if (resetMember) {
            resetMember(member);
        }
    }

    /**
     * Resets the participation state of a specific member in the feedback session.
     * This method removes the member from the list of session participants,
     * effectively resetting their participation status. If the member is not
     * currently part of the session or has already been reset, a {@code CommandException}
     * is thrown.
     *
     * @param member The {@code Member} whose participation is to be reset.
     *               This represents the guild member being removed from the session.
     * @throws CommandException If the member is not a participant in the session
     *                          or was already reset.
     */
    public synchronized void resetMember(Member member) throws CommandException {
        if (!members.remove(member)) {
            throw new CommandException("User is not a participant in the session yet or was already reset");
        }
    }

    public RestAction<Void> addSubmission(Member member, String url) {
        return validateSubmission(member, url)
            .onSuccess(success -> {
                synchronized (this) {
                    MessageCreateData message = createSubmissionMessage(member, url);
                    members.add(member);
                    boolean replaced = submissions.removeIf(submission -> submission.member().equals(member));

                    Submission submission = new Submission(member, url, message);
                    int multiplier = getMultiplier(member.getId());
                    submissions.addAll(Collections.nCopies(multiplier, submission));

                    this.component.getLogger().info(member.getUser(), "Submitted song: <%s> (×%.2f)", url,
                        ((float) multiplier) / BASELINE);
                    if (!replaced) {
                        submissionCount++;
                    }
                }
            });
    }

    /**
     * Validates the submission of a URL by a member and ensures all defined criteria are met.
     *
     * @param member The member who is submitting the URL.
     * @param url    The URL being submitted for validation.
     * @return A {@code RestAction<GuildVoiceState>} representing the result of the submission validation.
     * It either resolves successfully with the member's voice state or fails with an appropriate error.
     */
    private RestAction<Void> validateSubmission(Member member, String url) {
        Matcher matcher = URLUtil.matchUrl(url);
        if (matcher == null) {
            return new CompletedRestAction<>(member.getJDA(), new CommandException("Invalid URL"));
        }

        if (!URLUtil.isSafe(url)) {
            return new CompletedRestAction<>(member.getJDA(),
                new CommandException("Please provide a URL with HTTPS (https://)"));
        }

        String hostname = matcher.group("domain") + "." + matcher.group("tld");
        VariableSetComponent<String> websites = this.component.getWebsites();
        if (!websites.isEmpty() && !websites.contains(hostname)) {
            return new CompletedRestAction<>(member.getJDA(),
                new CommandException("The server does not accept links from that website (`%s`)".formatted(hostname)));
        }

        if (this.component.getBlacklist().contains(member.getUser())) {
            return new CompletedRestAction<>(member.getJDA(),
                new CommandException("You are blacklisted from submitting songs"));
        }

        if (members.contains(member) &&
            submissions.stream().noneMatch(submission -> submission.member().equals(member))) {
            return new CompletedRestAction<>(member.getJDA(),
                new CommandException("You have already submitted a song"));
        }

        VariableComponent<AudioChannel> voiceChannel = this.component.getVoiceChannel();
        if (voiceChannel.hasValue()) {
            String errorMessage = "You must be in the channel %s to submit songs".formatted(
                voiceChannel.getPrettyValue());
            return this.component.getGuild().retrieveMemberVoiceState(member)
                .onErrorFlatMap(e -> new CompletedRestAction<>(member.getJDA(), new CommandException(errorMessage)))
                .flatMap(voiceState -> {
                    AudioChannel connectedChannel = voiceState.getChannel();
                    if (connectedChannel == null || !voiceChannel.getValue().getId().equals(connectedChannel.getId())) {
                        return new CompletedRestAction<>(member.getJDA(), new CommandException(errorMessage));
                    }
                    return new CompletedRestAction<>(member.getJDA(), (Void) null);
                });
        }
        return new CompletedRestAction<>(member.getJDA(), (Void) null);
    }

    /**
     * Creates a message containing the details of a song submission.
     *
     * @param member The guild member who submitted the song.
     * @param url    The URL of the submitted song.
     * @return A {@code MessageCreateData} object representing the constructed message,
     * including an embed with submission details and an action row with a navigation button.
     */
    private MessageCreateData createSubmissionMessage(Member member, String url) {
        MessageCreateBuilder builder = new MessageCreateBuilder();

        EmbedBuilder embed = new EmbedBuilder()
            .setColor(ColorUtil.GOLD)
            .setTimestamp(Instant.now())
            .setAuthor(member.getEffectiveName(), null, member.getEffectiveAvatarUrl())
            .addField("User", String.format("%s `(%s)`", member.getAsMention(), member.getId()), true)
            .addField("Submission", String.format("<%s>", url), true);

        builder.setEmbeds(embed.build());
        builder.addComponents(
            ActionRow.of(this.component.getNextButton().getEntity())
        );

        return builder.build();
    }

    private int getMultiplier(String userId) {
        int n2 = previousSessions[0].winnerIds().contains(userId)
            ? 2
            : previousSessions[0].submissionIds().contains(userId)
              ? 1 : 0;
        int n1 = previousSessions[1].winnerIds().contains(userId)
            ? 2
            : previousSessions[1].submissionIds().contains(userId)
              ? 1 : 0;
        return MULTIPLIERS[n2][n1];
    }

    /**
     * Retrieves the submission URL associated with the specified member.
     *
     * @param member the member whose submission URL is to be retrieved
     * @return the URL of the submission if it exists, or null if no submission is found for the given member
     */
    public synchronized @Nullable String getSubmission(Member member) {
        return submissions.stream()
            .filter(submission -> submission.member().equals(member))
            .findFirst()
            .map(Submission::url)
            .orElse(null);
    }

    public RestAction<MessageEditData> handleNextSubmission(Message message) {
        return getNextSubmission()
            .map(submissionVoiceState -> {
                Submission submission = submissionVoiceState.submission();
                GuildVoiceState voiceState = submissionVoiceState.voiceState();

                component.getWinRole().accept(role ->
                    transferWinRole(role, this.currentWinner, submission.member())
                );

                this.currentWinner = submission.member();
                this.winnerIds.add(this.currentWinner.getId());

                if (voiceState != null) {
                    component.getVoiceChannel().accept(channel ->
                        inviteToStage(voiceState, channel)
                    );
                }

                component.getChatChannel().accept(channel -> {
                    if (channel.canTalk()) {
                        sendWinMessage(channel, submission.member());
                    }
                });

                component.getSubmissionChannel().accept(channel -> {
                    if (channel.canTalk()) {
                        channel.sendMessage(submission.message()).queue();
                    }
                });

                return editSubmissionMessage(message);
            });
    }

    @NotNull
    private RestAction<SubmissionVoiceState> getNextSubmission() {
        JDA jda = component.getGuild().getJDA();
        if (submissionCount == 0) {
            return new CompletedRestAction<>(jda, new CommandException("No songs have been submitted yet"));
        }
        if (submissions.isEmpty()) {
            return new CompletedRestAction<>(jda, new CommandException("There are no submissions left in the queue"));
        }

        Collections.shuffle(submissions);

        return getSubmissionAction(jda);
    }

    private RestAction<SubmissionVoiceState> getSubmissionAction(JDA jda) {
        return new DeferredRestAction<>(jda, SubmissionVoiceState.class, () -> null, () -> {
            if (this.submissions.isEmpty()) {
                return new CompletedRestAction<>(jda,
                    new CommandException("None of the participants are connected to the voice channel"));
            }

            VariableComponent<AudioChannel> voiceChannel = component.getVoiceChannel();

            Submission submission = this.submissions.removeFirst();
            this.submissions.removeAll(Collections.singleton(submission));

            if (!voiceChannel.hasValue()) {
                return new CompletedRestAction<>(jda, new SubmissionVoiceState(submission, null));
            }

            return component.getGuild().retrieveMemberVoiceState(submission.member())
                .onErrorMap(e -> null)
                .flatMap(voiceState -> {
                    if (voiceState == null) {
                        this.component.getLogger().info("Skipped submission by %s",
                            submission.member().getAsMention());
                        return getSubmissionAction(jda); // recursion
                    }

                    AudioChannel connectedChannel = voiceState.getChannel();
                    if (connectedChannel == null ||
                        !voiceChannel.getValue().getId().equals(connectedChannel.getId())) {
                        this.component.getLogger().info("Skipped submission by %s",
                            submission.member().getAsMention());
                        return getSubmissionAction(jda); // recursion
                    }

                    return new CompletedRestAction<>(jda, new SubmissionVoiceState(submission, voiceState));
                });
        });
    }

    /**
     * Invites a user to speak on a stage channel if the specified voice state and the current channel conditions are met.
     * <p>
     * This method evaluates whether the given voice state is non-null, the associated voice channel is set,
     * and the channel type is a stage channel. If all conditions are satisfied, an invitation is sent to
     * the user represented by the voice state to join as a speaker on the stage channel.
     *
     * @param voiceState The {@code GuildVoiceState} object representing the user's voice state in the guild.
     *                   The invitation is sent to the user associated with this state if eligible.
     */
    private void inviteToStage(GuildVoiceState voiceState, AudioChannel channel) {
        if (channel.getType().equals(ChannelType.STAGE)) {
            try {
                voiceState.inviteSpeaker().onErrorMap(e -> null).queue();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Sends a win message to the specified text channel, mentioning the winning member
     * and including an embed indicating their victory.
     *
     * @param winChannel The {@code TextChannel} where the win message will be sent.
     * @param member     The {@code Member} who has won and will be mentioned in the message.
     */
    private void sendWinMessage(TextChannel winChannel, Member member) {
        winChannel.sendMessage(new MessageCreateBuilder()
            .setContent(member.getAsMention())
            .addEmbeds(new EmbedBuilder()
                .setColor(ColorUtil.GOLD)
                .setDescription("\uD83C\uDFC6 You won!")
                .build()
            ).build()
        ).queue();
    }

    /**
     * Edits the components of a given message based on the presence of buttons with the "DANGER" style.
     * If no "DANGER" buttons are present, the components of the message are cleared.
     * Otherwise, only the "DANGER" buttons are retained in the message's components.
     *
     * @param message The {@code Message} object whose components are to be modified.
     * @return A {@code MessageEditData} object containing the updated message data.
     */
    private MessageEditData editSubmissionMessage(Message message) {
        MessageEditBuilder builder = MessageEditBuilder.fromMessage(message);
        List<Button> getDangerButtons = builder.getComponents().stream()
            .filter(c -> c.getType().equals(Component.Type.ACTION_ROW))
            .map(MessageTopLevelComponentUnion::asActionRow)
            .flatMap(row -> row.getButtons().stream())
            .filter(b -> b.getStyle().equals(ButtonStyle.DANGER))
            .toList();
        if (getDangerButtons.isEmpty()) {
            builder.setComponents();
        } else {
            builder.setComponents(ActionRow.of(getDangerButtons));
        }

        builder.setEmbeds(builder.getEmbeds().stream()
            .map(embed ->
                new EmbedBuilder(embed).setColor(ColorUtil.TRANSPARENT).build()
            )
            .toList()
        );
        return builder.build();
    }

    @Override
    public void supplyStatus(StatusCollector collector) {
        collector.addString("Submissions", String.valueOf(submissionCount))
            .addString("Queue size", String.valueOf(submissions.size()));
    }

    public synchronized List<Member> getQueuedMembers() {
        return submissions.stream().map(Submission::member).toList();
    }

    public Set<String> getWinnerIds() {
        return winnerIds;
    }

    public Set<String> getSubmissionIds() {
        return members.stream().map(ISnowflake::getId).collect(Collectors.toSet());
    }

    private record Submission(Member member, String url, MessageCreateData message) {}

    private record SubmissionVoiceState(Submission submission, @Nullable GuildVoiceState voiceState) {}

}
