package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
public interface VotingService {


    /**
     * Handles the voting
     *
     * @param voter
     * @param pollId non-null, existing pollId
     * @param pollItemId non-null, existing pollItemId
     * @throws javax.persistence.NoResultException if a pollEntity is not found
     */
    Vote vote(User voter, String pollId, String pollItemId);


    /**
     *
     *
     * @param areaId
     * @param pollId existing pollId
     * @return
     */
    VotingResult<PollItem> getPollResult(String areaId, String pollId);

     /**
      *
      *
      * @param voter
      * @param pollId existing pollId
      * @return Collection of PollItem ids for which the voter with id {@code voterId} has voted or empty collection
      * if no vote has been made or voterId/pollId does not exist
      */
     List<String> getVotedByVoter(User voter, String pollId);

     List<Vote> getVotesForPoll(String areaId, String pollId);

    void replaceVote(Set<Vote> forRemoval, Vote acceptedVote);

//    void revokeVote(String areaId, String voteId);
}
