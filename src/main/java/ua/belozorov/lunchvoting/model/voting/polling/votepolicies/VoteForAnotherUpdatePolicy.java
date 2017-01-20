package ua.belozorov.lunchvoting.model.voting.polling.votepolicies;

import ua.belozorov.lunchvoting.exceptions.VoteChangeNotAllowedException;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.ContinueCheckDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.UpdateDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * <h2>A {@code VotePolicy} implementation that implements updating a vote within a defined time period</h2>
 * Updating a vote means that a subset of previously accepted votes is removed
 * (for this policy subset will always contain only one vote for removal) and
 * the current vote is accepted.
 * The rules of this policy:
 * <ul>
 *     <li>A vote is subject to this policy if it is a vote for an item that the voter hasn't voted for earlier</li>
 *     <li>A vote time is after poll start and before time threshold for updating a vote</li>
 *     <li>If VoteIntention has a set of votes that have been made earlier, the latest vote will be submitted for removal</li>
 * </ul>
 * @author vabelozorov on 19.01.17.
 */
public final class VoteForAnotherUpdatePolicy implements VotePolicy {

    private final TimeConstraint timeConstraint;
    private final Set<Consumer<VoteIntention>> validators;

    public VoteForAnotherUpdatePolicy(TimeConstraint timeConstraint) {
        this.timeConstraint = timeConstraint;
        this.validators = this.registerValidators();
    }

    private Set<Consumer<VoteIntention>> registerValidators() {
        Set<Consumer<VoteIntention>> validators = new HashSet<>();
        validators.add(this::withinTimeThreshold);
        return validators;
    }

    private void withinTimeThreshold(VoteIntention intention) {
        LocalDateTime voteTime = intention.getTime();
        if ( ! this.timeConstraint.isInTimeToChangeVote(voteTime)) {
            throw new VoteChangeNotAllowedException(intention);
        }
    }

    @Override
    public VotePolicyDecision checkCompliance(VoteIntention intention) {
        if (intention.isVoteAgainForNewItem()) {
            validators.forEach(validator -> validator.accept(intention));
            HashSet<Vote> forRemoval = new HashSet<>(Collections.singletonList(intention.getLatestVote()));
            return new UpdateDecision(intention.toVote(), forRemoval);
        }
        return new ContinueCheckDecision();
    }
}
