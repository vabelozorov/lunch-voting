package ua.belozorov.lunchvoting.repository.voting;

import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
public interface PollRepository {

    LunchPlacePoll save(LunchPlacePoll poll);

    boolean removePoll(String areaId, String pollId);

    LunchPlacePoll get(String pollId);

    LunchPlacePoll get(String areaId, String pollId);

    LunchPlacePoll getWithPollItems(String pollId);

    LunchPlacePoll getWithPollItems(String areaId, String pollId);

    List<LunchPlacePoll> getAll(String areaId);

    List<LunchPlacePoll> getPollByActivePeriod(String areaId, LocalDateTime startDate, LocalDateTime endDate);

    LunchPlacePoll getWithPollItemsAndVotes(String areaId, String pollId);

    List<LunchPlacePoll> getFuturePolls(String areaId);

    List<LunchPlacePoll> getPastPolls(String areaId);

    List<Vote> getFullVotesForPoll(String areaId, String pollId);

    Boolean isActive(String areaId, String pollId);

    Vote save(Vote vote);

    Vote getFullVote(String voterId, String voteId);

    /**
     * Searches for a voter's vote assuming that only one vote from a voter can be given in a particular poll
     *
     * @param areaId
     * @param voterId
     * @param pollId
     * @return
     */
    Vote getFullVoteInPoll(String areaId, String voterId, String pollId);

    List<String> getVotedByVoter(String areaId, String pollId, String voterId);

    void remove(Vote vote);

    Vote getVote(String voterId, String voteId);

    void remove(Set<Vote> forRemoval);
}
