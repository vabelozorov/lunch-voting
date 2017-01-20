package ua.belozorov.lunchvoting.model.voting.polling.votepolicies;

import ua.belozorov.lunchvoting.exceptions.MultipleVotePerItemException;
import ua.belozorov.lunchvoting.exceptions.PollNotActiveException;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.ContinueCheckDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
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
 * @author vabelozorov on 19.01.17.
 */
public final class CommonPolicy implements VotePolicy {

    private final TimeConstraint timeConstraint;
    private final Set<Consumer<VoteIntention>> validators;

    public CommonPolicy(final TimeConstraint timeConstraint) {
        this.timeConstraint = timeConstraint;
        this.validators = this.registerValidators();
    }

    @Override
    public VotePolicyDecision checkCompliance(VoteIntention intention) {
        this.validators.forEach(validator -> validator.accept(intention));
        return new ContinueCheckDecision();
    }

    private Set<Consumer<VoteIntention>> registerValidators() {
        return new HashSet<>(Arrays.asList(this::oneVotePerItem, this::isRunning));
    }

    private void oneVotePerItem(VoteIntention intention) {
        if (intention.isVoteAgainForCurrentItem()) {
            throw new MultipleVotePerItemException(intention);
        }
    }

    private void isRunning(VoteIntention intention) {
        LocalDateTime voteTime = intention.getTime();
        if ( ! this.timeConstraint.isPollActive(voteTime)) {
            throw new PollNotActiveException(intention);
        }
    }
}
