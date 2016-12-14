package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.JsonUtils;
import ua.belozorov.lunchvoting.model.voting.Poll;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
public class VotingControllerTest extends AbstractControllerTest {

    @Test
    public void testSetPollDefaultTimeInternal() throws Exception {

    }

    @Test
    public void testSetPollDefaultTimeInternal1() throws Exception {

    }

    @Test
    public void testVote() throws Exception {

    }

    public static final String REST_URL = VotingController.REST_URL;

    @Test
    public void testGetRunningMenuPoll() throws Exception {
        MvcResult result = mockMvc.perform(get(REST_URL ))
                .andExpect(status().isOk())
                .andReturn();

        Poll poll = jsonUtils.fromMvcResultBody(result, Poll.class);
    }
}