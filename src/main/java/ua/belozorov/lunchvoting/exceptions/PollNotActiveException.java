package ua.belozorov.lunchvoting.exceptions;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.voting.VoteIntention;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.12.16.
 */
@Getter
public class PollNotActiveException extends RuntimeException {
    private final VoteIntention intention;

    public PollNotActiveException(final VoteIntention intention) {
        this.intention = intention;
    }
}
