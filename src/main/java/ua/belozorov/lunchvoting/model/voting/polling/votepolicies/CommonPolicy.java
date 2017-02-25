package ua.belozorov.lunchvoting.model.voting.polling.votepolicies;

import com.google.common.collect.ImmutableSet;
import ua.belozorov.lunchvoting.exceptions.PollException;
import ua.belozorov.lunchvoting.exceptions.VotePolicyException;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.ContinueCheckDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <h2>A {@code VotePolicy} implementation that implements basic checks</h2>
 * Policy rules are the following:
 * <ul>
 *     <li>Forbids multiple voting for the same item from one voter,
 *     in which case {@code MultipleVotePerItemException} is thrown</li>
 *     <li>Forbids voting for a poll that is not active</li>
 *     <li>If above rules are passed, the right to made a decision is passed to the next policy</li>
 * </ul>
 *
 * Created on 19.01.17.
 */
public final class CommonPolicy implements VotePolicy {

    private final TimeConstraint timeConstraint;
    private final Set<Consumer<VoteIntention>> validators;

    public CommonPolicy(TimeConstraint timeConstraint) {
        this.timeConstraint = timeConstraint;
        this.validators = this.registerValidators();
    }

    @Override
    public VotePolicyDecision checkCompliance(VoteIntention intention) {
        this.validators.forEach(validator -> validator.accept(intention));
        return new ContinueCheckDecision();
    }

    private Set<Consumer<VoteIntention>> registerValidators() {
        return ImmutableSet.of(this::oneVotePerItem, this::isRunning);
    }

    private void oneVotePerItem(VoteIntention intention) {
        if (intention.isVoteAgainForCurrentItem()) {
            throw new VotePolicyException(ErrorCode.MULTIPLE_VOTE_PER_SAME_ITEM);
        }
    }

    private void isRunning(VoteIntention intention) {
        LocalDateTime voteTime = intention.getTime();
        if ( ! this.timeConstraint.isPollActive(voteTime)) {
            throw new VotePolicyException(ErrorCode.POLL_NOT_ACTIVE);
        }
    }
}
