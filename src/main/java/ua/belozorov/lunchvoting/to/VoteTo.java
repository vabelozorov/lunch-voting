package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
public class VoteTo {
    private String id;
    private String voterId;
    private String pollId;
    private String itemId;

    public VoteTo(Vote vote) {
        this.id = vote.getId();
        this.voterId = vote.getVoterId();
        this.pollId = vote.getPoll().getId();
        this.itemId = vote.getPollItem().getId();
    }
}