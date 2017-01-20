package ua.belozorov.lunchvoting.model.voting.polling;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.01.17.
 */
public interface VoteIntention {
    boolean isVoteAgainForCurrentItem();
    boolean isVoteAgainForNewItem();

    boolean hasVotedEarlier();

    LocalDateTime getTime();
    PollItem getPollItem();

    Set<Vote> getExistingVotes();

    Vote getLatestVote();

    Vote toVote();
}
