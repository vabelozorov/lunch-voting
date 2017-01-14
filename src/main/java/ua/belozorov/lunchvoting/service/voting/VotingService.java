package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.voting.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.Poll;

import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
public interface VotingService {

    /**
     * Creates a Poll instance where PollItems are a set of LunchPlace instances and each such instance has a Menu
     * for today
     * @return ID of the created Poll instance
     */
   String createPollForTodayMenus();

    /**
     * Handles the voting
     * @param voterId non-null, existing voterId
     * @param pollId non-null, existing pollId
     * @param pollItemId non-null, existing pollItemId
     */
    void vote(String voterId, String pollId, String pollItemId);

    /**
     * Returns fully initialized Poll instance
     * @param pollId non-null, valid pollId
     * @return
     */
    Poll getPollFullDetails(String pollId);

    /**
     * Returns a Poll instance with one selected PollItem with complete information about its item
     * @param pollId non-null, valid pollId
     * @param pollItemId non-null, size > 0
     * @return
     */
    Poll getPollItemDetails(String pollId, String pollItemId);

    /**
     * Returns a Poll instance with a subset of PollItems, each having a complete information about its item
     * @param pollId non-null, valid pollId
     * @param pollItemIds non-null, size > 0
     * @return Poll instance
     */
    Poll getPollItemDetails(String pollId, Collection<String> pollItemIds);
}
