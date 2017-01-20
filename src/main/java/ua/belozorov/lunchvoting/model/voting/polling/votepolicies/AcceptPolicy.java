package ua.belozorov.lunchvoting.model.voting.polling.votepolicies;

import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.AcceptDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.ContinueCheckDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

/**
 * <h2>A @code VotePolicy} implementation that defines rules for accepting a vote</h2>
 * Accepting a vote means that a vote is added to a set of votes for this poll and no other vote is removed
 * If the voter hasn't voted for the poll before, the policy ends the checking sequence with accepting the vote,
 * otherwise the right to made a decision is passed to the next policy
 *
 * @author vabelozorov on 19.01.17.
 */
public class AcceptPolicy implements VotePolicy {

    @Override
    public VotePolicyDecision checkCompliance(VoteIntention intention) {
        return intention.hasVotedEarlier() ? new ContinueCheckDecision() : new AcceptDecision(intention.toVote());
    }
}
