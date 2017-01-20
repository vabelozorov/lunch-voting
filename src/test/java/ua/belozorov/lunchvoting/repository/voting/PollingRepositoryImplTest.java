package ua.belozorov.lunchvoting.repository.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 16.01.17.
 */
public class PollingRepositoryImplTest extends AbstractServiceTest {

    @Autowired
    private PollingRepository repository;

    @Test
    public void testGetPollWithVotesAndEmptyPollItems() throws Exception {
        LunchPlacePoll poll = repository.getPollWithVotesAndEmptyPollItems(testPolls.getActivePoll().getId());
        System.out.println();
    }

}