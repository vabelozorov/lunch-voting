package ua.belozorov.lunchvoting.exceptions;

import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 19.01.17.
 */
public class NoVotePolicyMatchException extends RuntimeException {
    private final VoteIntention intention;

    public NoVotePolicyMatchException(VoteIntention intention) {
        this.intention = intention;
    }
}
