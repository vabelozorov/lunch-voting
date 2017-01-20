package ua.belozorov.lunchvoting.model.voting.polling;

import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.time.LocalDate;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.01.17.
 */
public interface Poll {
    VotePolicyDecision registerVote(String voterId, String pollItemId);
    Set<PollItem> getPollItems();
    LocalDate getMenuDate();
}
