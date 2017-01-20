package ua.belozorov.lunchvoting.repository.voting;

import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Collection;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
public interface PollingRepository {

    void savePoll(LunchPlacePoll poll);

    LunchPlacePoll getPollWithVotesAndEmptyPollItems(String id);

    /**
     * Searches for a voter's vote assuming that only one vote from a voter can be given in a particular poll
     * @param voterId
     * @param pollId
     * @return
     */
    Vote getVoteInPoll(String voterId, String pollId);

    PollItem getPollItem(String pollId, String pollItemId);

    LunchPlacePoll getPollAndPollItem(String pollId, Collection<String> pollItemId);

    LunchPlacePoll getPollAndPollItem(String pollId, String pollItemId);

    LunchPlacePoll getFullPoll(String pollId);

    void saveVote(Vote vote);

    void removeVote(Vote vote);

    void removeVotes(Set<Vote> forRemoval);

    void replaceVote(Set<Vote> forRemoval, Vote acceptedVote);
}
