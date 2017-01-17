package ua.belozorov.lunchvoting.repository.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.voting.LunchPlacePoll;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import static org.junit.Assert.*;

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
        LunchPlacePoll poll = repository.getPollWithVotesAndEmptyPollItems(testPolls.getPoll2().getId());
        System.out.println();
    }

}