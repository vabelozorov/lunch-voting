package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.mocks.ServiceMocks;
import ua.belozorov.lunchvoting.model.voting.PollVoteResult;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.service.voting.PollService;
import ua.belozorov.lunchvoting.web.security.AuthorizedUser;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.VoteTo;

import java.util.*;

import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 05.02.17.
 */
@ContextConfiguration(classes = ServiceMocks.class)
public class VotingControllerTest extends AbstractControllerTest {
    public static final String REST_URL = VotingController.REST_URL;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VotingService votingService;

    public final String areaId = testAreas.getFirstAreaId();

    @Override
    public void beforeTest() {
    }

    @Test
    public void voteForPoll() throws Exception {
        String pollId = "poll_id";
        String itemId = "item_id";
        Vote vote = testVotes.getVotesForActivePoll().iterator().next();

        when(votingService.vote(GOD, pollId, itemId)).thenReturn(vote);

        String actual = mockMvc
                .perform(
                    post(REST_URL + "/polls/{pollid}/{pollItemId}", areaId, pollId, itemId)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(csrf())
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
                    get(REST_URL + "/polls/{pollid}", areaId, id)
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
                        get(REST_URL + "/polls/{pollId}", areaId, id)
                        .param("item", "")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
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
    public void getVotedByVoter() throws Exception {
        String id = "poll_id";

        List<String> votedItemIds = Arrays.asList("One", "Two");
        when(votingService.getVotedForPollByVoter(VOTER, id)).thenReturn(votedItemIds);

        String actual = mockMvc
                .perform(
                        get(REST_URL + "/polls/{pollId}", areaId, id)
                        .param("voter", "")
                        .accept(MediaType.APPLICATION_JSON)
                        .with(voter())
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();
        Map<String, Object> objectProperties = new LinkedHashMap<>();
        objectProperties.put("pollId", id);
        objectProperties.put("voterId", VOTER_ID);
        objectProperties.put("votedItems", votedItemIds);
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
                        .with(csrf())
                        .with(voter())
                )
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse().getContentAsString();

        verify(votingService).revokeVote(VOTER_ID, voteId);
    }
}