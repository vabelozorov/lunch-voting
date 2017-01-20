package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.12.16.
 */
@Getter(AccessLevel.PACKAGE)
final class VoteIntentionImpl implements VoteIntention {
    private final String voterId;
    private final PollItem pollItem;
    private final Set<Vote> existingVotes;
    private final LocalDateTime time = LocalDateTime.now();

    VoteIntentionImpl(String voterId, PollItem pollItem, Set<Vote> existingVotes) {
        this.voterId = Objects.requireNonNull(voterId, "voterId must not be null");
        this.pollItem = Objects.requireNonNull(pollItem, "pollItem must not be null");
        this.existingVotes = Objects.requireNonNull(Collections.unmodifiableSet(existingVotes), "pollItem must not be null");
    }

    @Override
    public boolean isVoteAgainForCurrentItem() {
        return existingVotes.stream()
                                .filter(vote -> vote.getPollItem().equals(this.pollItem))
                                .count() > 0;
    }

    @Override
    public boolean isVoteAgainForNewItem() {
        return this.hasVotedEarlier()  && ! this.isVoteAgainForCurrentItem();
    }

    @Override
    public boolean hasVotedEarlier() {
        return ! this.existingVotes.isEmpty();
    }

    @Override
    public LocalDateTime getTime() {
        return this.time;
    }

    @Override
    public PollItem getPollItem() {
        return this.pollItem;
    }

    @Override
    public Set<Vote> getExistingVotes() {
        return this.existingVotes;
    }

    @Override
    public Vote getLatestVote() {
        return this.existingVotes.stream()
                .sorted(Comparator.comparing(Vote::getVoteTime).reversed())
                .findFirst()
                .orElse(null);
    }

    @Override
    public Vote toVote() {
        return new Vote(this.voterId, this.pollItem.getPoll(), this.pollItem, this.time);
    }
}