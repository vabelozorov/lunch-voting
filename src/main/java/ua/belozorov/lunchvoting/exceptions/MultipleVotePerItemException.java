package ua.belozorov.lunchvoting.exceptions;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;

/**
 * <h2>Thrown when the 2nd and subsequent votes are made for the same item</h2>
 *
 * @author vabelozorov on 09.12.16.
 */
@Getter
public class MultipleVotePerItemException extends RuntimeException {
    private final VoteIntention intention;

    public MultipleVotePerItemException(final VoteIntention intention) {
        this.intention = intention;
    }
}
