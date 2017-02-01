package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

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
   Poll createPollForTodayMenus();

    /**
     * Handles the voting
     * @param voterId non-null, existing voterId
     * @param pollId non-null, existing pollId
     * @param pollItemId non-null, existing pollItemId
     * @throws javax.persistence.NoResultException if a pollEntity is not found
     */
    Vote vote(String voterId, String pollId, String pollItemId);

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

    /**
     *
     * @param pollId existing pollId
     * @return
     */
    VotingResult<PollItem> getPollResult(String pollId);

     /**
      *
      * @param pollId existing pollId
      * @param voterId existing voterId
      * @return Collection of PollItem ids for which the voter with id {@code voterId} has voted or empty collection
      * if no vote has been made or voterId/pollId does not exist
      */
     Collection<String> getVotedForVoter(String pollId, String voterId);
}
