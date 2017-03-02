package ua.belozorov.lunchvoting.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ua.belozorov.lunchvoting.model.voting.PollVoteResult;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.VoteTo;

import java.util.*;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**
 *
 * Created on 05.02.17.
 */
public class VotingControllerTest extends AbstractControllerTest {
    public static final String REST_URL = VotingController.REST_URL;

    @Autowired
    private VotingService votingService;

    public final String areaId = testAreas.getFirstAreaId();

    @Before
    public void setUp() throws Exception {
        Mockito.reset(votingService);
    }

    @Test
    public void voteForPoll() throws Exception {
        String pollId = "poll_id";
        String itemId = "item_id";
        Vote vote = testVotes.getVotesForActivePoll().iterator().next();

        when(votingService.vote(GOD, pollId, itemId)).thenReturn(vote);

        String actual = mockMvc
                .perform(
                    post(REST_URL, areaId)
                    .param("pollId", pollId)
                    .param("pollItemId", itemId)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                                        .with(god()) // actually VOTER right is enough, but GOD is the only who hasn't voted
                )
                .andExpect(status().isCreated())
                .andReturn()
                    .getResponse().getContentAsString();

        verify(votingService).vote(GOD, pollId, itemId);

        String expected = jsonUtils.toJson(new VoteTo(vote, true));

        assertJson(expected, actual);
    }

    @Test
    public void getVotesForPoll() throws Exception {
        String id = "poll_id";

        when(votingService.getFullVotesForPoll(areaId, id))
                .thenReturn(new ArrayList<>(testVotes.getVotesForPastPoll()));
        String actual = mockMvc
                .perform(
                    get(REST_URL, areaId)
                    .param("pollId", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn()
                    .getResponse().getContentAsString();

        verify(votingService).getFullVotesForPoll(areaId, id);

        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("votes", VotingController.convertIntoTo(testVotes.getVotesForPastPoll(), false));
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void getPollResult() throws Exception {
        String id = "poll_id";

        when(votingService.getPollResult(areaId, id))
                .thenReturn(new PollVoteResult<>(testPolls.getPastPoll(), Vote::getPollItem));

        String actual = mockMvc
                .perform(
                        get(REST_URL + "/results", areaId)
                        .param("pollId", id)
                        .param("type", "item")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn()
                    .getResponse().getContentAsString();

        Map<String, Object> objectProperties = this.getExpectedPollResultByItem(id);

        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void getVotedByVoter() throws Exception {
        String id = "poll_id";

        List<String> votedItemIds = Arrays.asList("One", "Two");
        when(votingService.getVotedForPollByVoter(VOTER, id)).thenReturn(votedItemIds);

        String actual = mockMvc
                .perform(
                        get(REST_URL, areaId, id)
                        .param("pollId", id)
                        .param("filterBy", "voter")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("voterId", VOTER_ID);
        objectProperties.put("votedPollItems", votedItemIds);
        String expected = jsonUtils.toJson(objectProperties);

        assertJson(expected, actual);
    }

    @Test
    public void voterRevokesVote() throws Exception {
        String voteId = "vote_id";
        String actual = mockMvc
                .perform(
                        delete(REST_URL + "/{voteId}", areaId, voteId)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse().getContentAsString();

        verify(votingService).revokeVote(VOTER_ID, voteId);
    }

    private Map<String, Object> getExpectedPollResultByItem(String pollId) {
        Map<String, Object> resultMap1 = new LinkedHashMap<>();
        resultMap1.put("pollItemId", testPolls.getPastPollPollItem1().getId());
        resultMap1.put("itemId", testPolls.getPastPollPollItem1().getItemId());
        resultMap1.put("count", 3);
        Map<String, Object> resultMap2 = new LinkedHashMap<>();
        resultMap2.put("pollItemId", testPolls.getPastPollPollItem2().getId());
        resultMap2.put("itemId", testPolls.getPastPollPollItem2().getItemId());
        resultMap2.put("count", 2);
        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", pollId);
        objectProperties.put("result", Arrays.asList(resultMap1, resultMap2));;
        return objectProperties;
    }
}