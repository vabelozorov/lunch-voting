package ua.belozorov.lunchvoting.model.voting;

import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.List;
import java.util.Map;

/**
 * <h2></h2>
 * @param <I> classification item of VoteStatistics implementation
 * Created on 10.12.16.
 */
public interface VotingResult<I> {

    Map<I, Integer> countPerItem();

    Map<I, List<Vote>> votesForItem();

    List<I> getWinners();

    List<Vote> votesForItem(I item);
}
