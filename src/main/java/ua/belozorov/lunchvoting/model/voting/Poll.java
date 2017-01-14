package ua.belozorov.lunchvoting.model.voting;

import java.time.LocalDate;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.01.17.
 */
public interface Poll {
    VoteDecision verify(VoteIntention intention);
    Set<PollItem> getPollItems();
    LocalDate getMenuDate();
}
