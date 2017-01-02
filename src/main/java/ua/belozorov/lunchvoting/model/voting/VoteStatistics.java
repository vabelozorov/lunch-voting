package ua.belozorov.lunchvoting.model.voting;

import java.util.List;
import java.util.Map;

/**
 * <h2></h2>
 * @param <I> classification item of VoteStatistics implementation
 * @author vabelozorov on 10.12.16.
 */
public interface VoteStatistics<I> {

    Map<I, Integer> countPerItem();

    Map<I, List<Vote>> votesPerItem();
}
