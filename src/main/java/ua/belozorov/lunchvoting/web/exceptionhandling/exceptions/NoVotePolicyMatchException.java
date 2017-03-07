package ua.belozorov.lunchvoting.web.exceptionhandling.exceptions;

import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

/**

 *
 * Created on 18.02.17.
 */
public class NoVotePolicyMatchException extends RuntimeException implements ApplicationException {
    private final ErrorCode code;
    private final VoteIntention intention;

    public NoVotePolicyMatchException(VoteIntention intention) {
        this.code = ErrorCode.NO_VOTE_POLICY_MATCH;
        this.intention = intention;
    }

    @Override
    public ErrorCode getErrorCode() {
        return code;
    }
}
