package ua.belozorov.lunchvoting.exceptions;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.voting.VoteIntention;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.12.16.
 */
@Getter
public class VoteChangeNotAllowedException extends RuntimeException {
    private final VoteIntention intention;

    public VoteChangeNotAllowedException(final VoteIntention intention) {
        this.intention = intention;
    }
}
