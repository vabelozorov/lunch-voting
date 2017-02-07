package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.VoteTo;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD_ID;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 05.02.17.
 */
public class VotingControllerTest extends AbstractControllerTest {
    public static final String REST_URL = VotingController.VOTE_URL;

    @Autowired
    private VotingService votingService;

    @Test
    public void voteForPoll() throws Exception {
        String id = testPolls.getActivePoll().getId();
        String actual = mockMvc.perform(
                post("{base}/polls/{id}", REST_URL, id)
                        .param("voterId", GOD_ID)
                        .param("pollItemId", testPolls.getActivePollPollItem1().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                    .getResponse().getContentAsString();

        Vote vote = votingService.getVotesForPoll(id).stream()
                .filter(v -> v.getVoterId().equals(GOD_ID))
                .findAny().get();
        String expected = jsonUtils.toJson(new VoteTo(vote, true));

        assertJson(expected, actual);

    }

    @Test
    public void testGetVotesForPoll() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get("{base}/polls/{id}", REST_URL, id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                    .getResponse().getContentAsString();

        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("votes", VotingController.convertIntoTo(testVotes.getVotesForPastPoll(), false));
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetPollResult() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get("{base}/polls/{id}/result/perItem", REST_URL, id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                    .getResponse().getContentAsString();
        Map<String, Object> resultMap1 = new LinkedHashMap<>();
        resultMap1.put("id", testPolls.getPastPollPollItem1().getId());
        resultMap1.put("itemId", testPolls.getPastPollPollItem1().getItemId());
        resultMap1.put("count", 3);
        Map<String, Object> resultMap2 = new LinkedHashMap<>();
        resultMap2.put("id", testPolls.getPastPollPollItem2().getId());
        resultMap2.put("itemId", testPolls.getPastPollPollItem2().getItemId());
        resultMap2.put("count", 2);
        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("result", Arrays.asList(resultMap1, resultMap2));

        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void testGetVotedByVoter() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get("{base}/polls/{id}", REST_URL, id)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("voterId", VOTER_ID))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("voterId", VOTER_ID);
        objectProperties.put("votedItems", Arrays.asList(testPolls.getPastPollPollItem1().getId()));
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }
}