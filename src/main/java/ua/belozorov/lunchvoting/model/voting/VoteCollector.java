package ua.belozorov.lunchvoting.model.voting;

import java.util.List;
import java.util.function.Function;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.12.16.
 */
public interface VoteCollector {

     VoteCollector collect(List<Vote> votes);

     VoteCollector collect(Vote vote);

     <I> VoteStatistics<I> result(Function<Vote, I> voteClassifier);

     static Function<Vote, PollItem> pollItemClassifier() {
         return Vote::getPollItem;
    }

     static Function<Vote, String> voterIdClassifier() {
         return Vote::getVoterId;
    }
}
