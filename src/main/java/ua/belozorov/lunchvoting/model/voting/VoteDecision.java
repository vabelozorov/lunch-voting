package ua.belozorov.lunchvoting.model.voting;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
public final class VoteDecision {
    private final Vote vote;
    private final Decision decision;


    private VoteDecision(Vote vote, Decision decision) {
        this.vote = vote;
        this.decision = decision;
    }

    public boolean isAccept() {
        return decision.equals(Decision.ACCEPT);
    }

    public boolean isUpdate() {
        return decision.equals(Decision.UPDATE);
    }

    public Vote getVote() {
        return vote;
    }

    public static VoteDecision accept(Vote vote) {
        return new VoteDecision(vote, Decision.ACCEPT);
    }
    public static VoteDecision update(Vote vote) {
        return new VoteDecision(vote, Decision.UPDATE);
    }

    private enum Decision {
        ACCEPT,
        UPDATE,
    }
}
