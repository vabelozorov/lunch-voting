package ua.belozorov.lunchvoting.web;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.AuthorizedUser;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.service.voting.VotingService;
import ua.belozorov.lunchvoting.to.VoteTo;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD_ID;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 05.02.17.
 */
public class VotingControllerTest extends AbstractControllerTest {
    public static final String REST_URL = VotingController.REST_URL;

    @Autowired
    private VotingService votingService;

    public final String areaId = testAreas.getFirstAreaId();

    @Test
    public void voteForPoll() throws Exception {
        String id = testPolls.getActivePoll().getId();
        String actual = mockMvc.perform(
                post(REST_URL + "/polls/{pollid}/{pollItemId}", areaId, id, testPolls.getActivePollPollItem1().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                    .getResponse().getContentAsString();

        Vote vote = votingService.getFullVotesForPoll(areaId, id).stream()
                .filter(v -> v.getVoterId().equals(GOD_ID))
                .findAny().get();
        String expected = jsonUtils.toJson(new VoteTo(vote, true));

        assertJson(expected, actual);
    }

    @Test
    public void getVotesForPoll() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get(REST_URL + "/polls/{pollid}", areaId, id)
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
    public void getPollResult() throws Exception {
        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get(REST_URL + "/polls/{pollId}", areaId, id)
                        .param("item", "")
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
    public void getVotedByVoter() throws Exception {
        AuthorizedUser.authorize(VOTER);

        String id = testPolls.getPastPoll().getId();
        String actual = mockMvc.perform(
                get(REST_URL + "/polls/{pollId}", areaId, id)
                        .param("voter", "")
                        .accept(MediaType.APPLICATION_JSON))
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

    @Test
    public void voterRevokesVote() throws Exception {
        AuthorizedUser.authorize(VOTER);

        Vote voteToRevoke = testPolls.getPastPoll().getVotes().stream().filter(v -> v.getVoterId().equals(VOTER_ID)).findFirst().get();
        String actual = mockMvc.perform(
                delete(REST_URL + "/{voteId}", areaId, voteToRevoke.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse().getContentAsString();

        thrown.expect(NotFoundException.class);
        votingService.getVote(VOTER_ID, voteToRevoke.getId());
    }
}