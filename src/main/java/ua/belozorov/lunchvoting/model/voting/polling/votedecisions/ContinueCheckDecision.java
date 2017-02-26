package ua.belozorov.lunchvoting.model.voting.polling.votedecisions;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Set;

/**

 *
 * Created on 19.01.17.
 */
public class ContinueCheckDecision implements VotePolicyDecision {
    @Override
    public boolean isContinue() {
        return true;
    }

    @Override
    public boolean isAccept() {
        return false;
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public Vote getAcceptedVote() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Vote> votesToBeRemoved() {
        throw new UnsupportedOperationException();
    }
}
