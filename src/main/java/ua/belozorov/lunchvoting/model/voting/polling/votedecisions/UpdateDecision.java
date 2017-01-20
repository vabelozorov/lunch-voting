package ua.belozorov.lunchvoting.model.voting.polling.votedecisions;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 19.01.17.
 */
public final class UpdateDecision implements VotePolicyDecision {

    private final Vote accepted;
    private final Set<Vote> forRemoval;

    public UpdateDecision(Vote accepted, Set<Vote> forRemoval) {
        this.accepted = accepted;
        this.forRemoval = Collections.unmodifiableSet(forRemoval);
    }

    @Override
    public boolean isContinue() {
        return false;
    }

    @Override
    public boolean isAccept() {
        return false;
    }

    @Override
    public boolean isUpdate() {
        return true;
    }

    @Override
    public Vote getAcceptedVote() {
        return this.accepted;
    }

    @Override
    public Set<Vote> votesToBeRemoved() {
        return this.forRemoval;
    }
}
