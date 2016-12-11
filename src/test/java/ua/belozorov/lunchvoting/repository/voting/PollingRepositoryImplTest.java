package ua.belozorov.lunchvoting.repository.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import javax.persistence.Entity;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.model.voting.PollTestData.POLL1;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
public class PollingRepositoryImplTest extends AbstractServiceTest {

    @Autowired
    private PollingRepository repository;

    @Test
    public void testGetPollWithOneFullPollItem() throws Exception {
        Poll poll = repository.getPollAndPollItem(POLL1.getId(), POLL1.getPollItems().iterator().next().getId());
        System.out.println();
    }
}