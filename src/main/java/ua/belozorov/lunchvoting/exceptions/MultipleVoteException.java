package ua.belozorov.lunchvoting.exceptions;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.voting.VoteIntention;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
@Getter
public class MultipleVoteException extends RuntimeException {
    private final VoteIntention intention;

    public MultipleVoteException(final VoteIntention intention) {
        this.intention = intention;
    }
}
