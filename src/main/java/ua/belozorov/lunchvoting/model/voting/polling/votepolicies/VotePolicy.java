package ua.belozorov.lunchvoting.model.voting.polling.votepolicies;

import ua.belozorov.lunchvoting.model.voting.polling.VoteIntention;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;

/**
 * <h2></h2>
 *
 * Created on 19.01.17.
 */
public interface VotePolicy {
    VotePolicyDecision checkCompliance(VoteIntention intention);
}
