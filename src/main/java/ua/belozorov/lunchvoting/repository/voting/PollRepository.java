package ua.belozorov.lunchvoting.repository.voting;

import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
public interface PollRepository {

    void savePoll(LunchPlacePoll poll);

    boolean removePoll(String id);

    LunchPlacePoll get(String id);

    LunchPlacePoll getWithVotes(String id);

    List<LunchPlacePoll> getAllPolls();

    List<LunchPlacePoll> getPollByActivePeriod(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Searches for a voter's vote assuming that only one vote from a voter can be given in a particular poll
     * @param voterId
     * @param pollId
     * @return
     */
    Vote getVoteInPoll(String voterId, String pollId);

    void saveVote(Vote vote);

    void removeVote(Vote vote);

    void removeVotes(Set<Vote> forRemoval);

    void replaceVote(Set<Vote> forRemoval, Vote acceptedVote);

    Collection<String> getVotedByVoter(String pollId, String voterId);

    LunchPlacePoll getPollAndPollItemsAndVotes(String pollId);

    List<LunchPlacePoll> getFuturePolls();

    List<LunchPlacePoll> getPastPolls();

    Collection<Vote> getVotesForPoll(String pollId);

    Boolean isActive(String id);
}
