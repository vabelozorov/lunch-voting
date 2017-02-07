package ua.belozorov.lunchvoting.model.voting.polling;

import ua.belozorov.lunchvoting.model.base.Persistable;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.01.17.
 */
public interface Poll extends Comparable<Poll>, Persistable {
    VotePolicyDecision registerVote(String voterId, String pollItemId);
    List<PollItem> getPollItems();
    LocalDate getMenuDate();
    Set<Vote> getVotes();
}
