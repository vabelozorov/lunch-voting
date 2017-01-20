package ua.belozorov.lunchvoting.model.voting.polling.votedecisions;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 19.01.17.
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
        throw new NotImplementedException();
    }

    @Override
    public Set<Vote> votesToBeRemoved() {
        throw new NotImplementedException();
    }
}
