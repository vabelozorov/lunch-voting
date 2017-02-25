package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

/**
 * <h2></h2>
 *
 * Created on 31.01.17.
 */
public class VoteTo {
    private String id;
    private String voterId;
    private String pollId;
    private String itemId;

    public VoteTo(Vote vote, boolean includePollId) {
        this.id = vote.getId();
        this.voterId = vote.getVoterId();
        this.pollId = includePollId ? vote.getPoll().getId() : null;
        this.itemId = vote.getPollItem().getId();
    }
}
