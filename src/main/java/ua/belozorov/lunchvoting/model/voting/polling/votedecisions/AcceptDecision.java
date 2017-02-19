package ua.belozorov.lunchvoting.model.voting.polling.votedecisions;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 19.01.17.
 */
public final class AcceptDecision implements VotePolicyDecision {

    private final Vote vote;

    public AcceptDecision(Vote vote) {
        this.vote = vote;
    }

    @Override
    public boolean isContinue() {
        return false;
    }

    @Override
    public boolean isAccept() {
        return true;
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public Vote getAcceptedVote() {
        return this.vote;
    }

    @Override
    public Set<Vote> votesToBeRemoved() {
        throw new UnsupportedOperationException();
    }
}
