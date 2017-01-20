package ua.belozorov.lunchvoting.model.voting;

import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.Collection;
import java.util.function.Function;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.12.16.
 */
public interface VoteCollector {

     VoteCollector collect(Collection<Vote> votes);

     VoteCollector collect(Vote vote);

    PollingResult<PollItem> getByPollItem();

    <I> VoteStatistics<I> result(Function<Vote, I> voteClassifier);

     static Function<Vote, PollItem> pollItemClassifier() {
         return Vote::getPollItem;
    }

     static Function<Vote, String> voterIdClassifier() {
         return Vote::getVoterId;
    }
}
