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
     * Handles the voting
     * @param voterId non-null, existing voterId
     * @param pollId non-null, existing pollId
     * @param pollItemId non-null, existing pollItemId
     * @throws javax.persistence.NoResultException if a pollEntity is not found
     */
    Vote vote(String voterId, String pollId, String pollItemId);


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
     Collection<String> getVotedByVoter(String pollId, String voterId);

     Collection<Vote> getVotesForPoll(String pollId);
}
