package com.pascalnb.eddie.components.feedback;

import com.pascalnb.eddie.ColorUtil;
import com.pascalnb.eddie.URLUtil;
import com.pascalnb.eddie.components.StatusComponent;
import com.pascalnb.eddie.components.setting.VariableComponent;
import com.pascalnb.eddie.exceptions.CommandException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;

public class FeedbackSession implements StatusComponent {

    private final FeedbackComponent component;
    private final Set<Member> members = new HashSet<>();
    private final List<Submission> submissions = new ArrayList<>();
    private int submissionCount = 0;
    private Member currentWinner = null;

    public FeedbackSession(FeedbackComponent component) {
        this.component = component;
    }

    /**
     * Starts the feedback session by sending the initial messages to the appropriate channels.
     */
    public synchronized void start() {
        component.getChatChannel().apply(channel ->
            channel.sendMessage(
                component.getSubmitMenu().getEntity()
            ).queue()
        );

        component.getSubmissionChannel().apply(channel ->
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
        component.getWinRole().apply(role ->
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

    /**
     * Adds a new song submission to the list of submissions for the feedback session.
     * This method validates the submission, creates a message for the submission,
     * and updates the internal state of the session to reflect the new submission.
     *
     * @param member The guild member submitting the song. Validation ensures the member is eligible to submit.
     * @param url    The URL of the song being submitted. The URL is validated for safety and allowed domains.
     * @throws CommandException If the submission fails validation or other constraints are violated.
     */
    public synchronized void addSong(Member member, String url) throws CommandException {
        validateSubmission(member, url);
        MessageCreateData message = createSubmissionMessage(member, url);
        members.add(member);
        boolean replaced = submissions.removeIf(submission -> submission.member().equals(member));
        submissions.add(new Submission(member, url, message));
        if (!replaced) {
            submissionCount++;
        }
    }

    /**
     * Validates a song submission by ensuring all specified constraints are met.
     * Throws {@link CommandException} if the submission is invalid.
     *
     * @param member The guild member attempting to submit a song.
     * @param url    The URL of the song being submitted.
     * @throws CommandException if the member has already submitted a song,
     *                          the URL is invalid, the URL is unsafe (non-HTTPS),
     *                          the domain is not allowed, the member is blacklisted,
     *                          or the member is not in the required voice channel.
     */
    private void validateSubmission(Member member, String url) throws CommandException {
        Matcher matcher = URLUtil.matchUrl(url);
        if (matcher == null) {
            throw new CommandException("Invalid URL");
        }
        if (!URLUtil.isSafe(url)) {
            throw new CommandException("Please provide a URL with HTTPS (https://)");
        }
        String hostname = matcher.group("domain") + "." + matcher.group("tld");
        if (!this.component.getWebsites().contains(hostname)) {
            throw new CommandException("The server does not accept links from that website (`%s`)".formatted(hostname));
        }
        if (this.component.getBlacklist().contains(member.getUser())) {
            throw new CommandException("You are blacklisted from submitting songs");
        }
        VariableComponent<AudioChannel> voiceChannel = this.component.getVoiceChannel();
        if (voiceChannel.hasValue()) {
            String errorMessage = "You must be in the channel %s to submit songs".formatted(
                voiceChannel.getPrettyValue());
            GuildVoiceState voiceState = this.component.getGuild().retrieveMemberVoiceState(member)
                .onErrorMap(e -> null)
                .complete();
            if (voiceState == null) {
                throw new CommandException(errorMessage);
            }
            AudioChannel connectedChannel = voiceState.getChannel();
            if (!voiceChannel.getValue().equals(connectedChannel)) {
                throw new CommandException(errorMessage);
            }
        }
        if (members.contains(member) &&
            submissions.stream().noneMatch(submission -> submission.member().equals(member))) {
            throw new CommandException("You have already submitted a song");
        }
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

    /**
     * Handles the next song submission in the feedback session. This method updates the current winner,
     * assigns roles, sends messages to appropriate channels, and edits the interaction message.
     *
     * @param message The {@code Message} object representing the current submission message to be edited.
     * @param hook    The {@code InteractionHook} used for updating the original interaction message.
     * @throws CommandException If retrieving the next submission fails or if any validation errors occur.
     */
    public synchronized void handleNextSubmission(Message message, InteractionHook hook) throws CommandException {
        SubmissionVoiceState submissionVoiceState = getNextSubmission();  // throws CommandException on error
        Submission submission = submissionVoiceState.submission();
        GuildVoiceState voiceState = submissionVoiceState.voiceState();

        component.getWinRole().apply(role ->
            transferWinRole(role, this.currentWinner, submission.member())
        );
        this.currentWinner = submission.member();

        if (voiceState != null) {
            component.getVoiceChannel().apply(channel ->
                inviteToStage(voiceState, channel)
            );
        }

        component.getChatChannel().apply(channel -> {
            if (channel.canTalk()) {
                sendWinMessage(channel, submission.member());
            }
        });

        component.getSubmissionChannel().apply(channel -> {
            if (channel.canTalk()) {
                channel.sendMessage(submission.message()).queue();
            }
        });

        MessageEditData updatedSubmissionMessage = editSubmissionMessage(message);
        hook.editOriginal(updatedSubmissionMessage).queue();
    }

    /**
     * Retrieves the next eligible song submission along with the associated voice state of the user
     * who made the submission. The method randomly selects from the available submissions while ensuring
     * the submitter is connected to the correct voice channel. If no submissions are available or if
     * none of the submitters are connected to the required voice channel, a {@link CommandException}
     * is thrown.
     *
     * @return A {@code SubmissionVoiceState} object containing the next song submission and the related
     * voice state of the submitting member.
     * @throws CommandException if there are no submissions, the queue is empty, or none of the
     *                          participants are connected to the required voice channel.
     */
    @NotNull
    private SubmissionVoiceState getNextSubmission() throws CommandException {
        if (submissionCount == 0) {
            throw new CommandException("No songs have been submitted yet");
        }
        if (submissions.isEmpty()) {
            throw new CommandException("There are no submissions left in the queue");
        }

        VariableComponent<AudioChannel> voiceChannel = component.getVoiceChannel();
        GuildVoiceState voiceState = null;
        Submission submission = null;

        while (!submissions.isEmpty()) {
            Collections.shuffle(submissions);
            submission = submissions.removeFirst();
            if (!voiceChannel.hasValue()) {
                break;
            }

            voiceState = component.getGuild().retrieveMemberVoiceState(submission.member())
                .onErrorMap(e -> null)
                .complete();
            if (voiceState == null) {
                continue;
            }
            AudioChannel connectedChannel = voiceState.getChannel();
            if (!voiceChannel.getValue().equals(connectedChannel)) {
                continue;
            }

            submission = null;
        }

        if (submission == null) {
            throw new CommandException("None of the participants are connected to the voice channel");
        }

        return new SubmissionVoiceState(submission, voiceState);
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
        collector.addString("Submissions", String.valueOf(submissions.size()))
            .addString("Queue size", String.valueOf(submissions.size()));
    }

    private record Submission(Member member, String url, MessageCreateData message) {}

    private record SubmissionVoiceState(Submission submission, @Nullable GuildVoiceState voiceState) {}

}
