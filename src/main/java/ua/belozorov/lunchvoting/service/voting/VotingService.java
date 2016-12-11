package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.model.voting.PollingTimeInterval;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
public interface VotingService {

    void setPollingDefaultInterval(PollingTimeInterval interval);

    String createPollForTodayMenus();

    PollingTimeInterval getDefaultPollInterval();

    void vote(String voterId, String pollId, String pollItemId);

    Poll getPollItemDetails(String pollId, String pollItemId);
}
