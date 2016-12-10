package ua.belozorov.lunchvoting.model.voting;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.12.16.
 */
@Getter(AccessLevel.PACKAGE)
public final class VoteIntention {
    private final String voterId;
    private final String pollId;
    private final String pollItemId;
    private final Vote existingVote;
    private final LocalDateTime madeTime = LocalDateTime.now();

    public VoteIntention(String voterId, String pollId, String pollItemId, Vote existingVote) {
        this.voterId = Objects.requireNonNull(voterId, "voterId must not be null");
        this.pollId = Objects.requireNonNull(pollId, "pollId must not be null");
        this.pollItemId = Objects.requireNonNull(pollItemId, "pollItemId must not be null");

        if ( existingVote != null && ! existingVote.getPoll().getId().equals(this.pollId)) {
            throw new IllegalStateException("Existing vote has been madeTime for another poll");
        }
        this.existingVote = existingVote;
    }

    boolean hasVotedEarlier() {
        return existingVote != null;
    }

    boolean isForAnotherItem() {
        return hasVotedEarlier() && ! existingVote.getPollItem().getId().equals(this.pollItemId);
    }

    boolean isForTheSameItem() {
        return hasVotedEarlier() && existingVote.getPollItem().getId().equals(this.pollItemId);
    }
}