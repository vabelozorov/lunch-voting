package ua.belozorov.lunchvoting.model.voting;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTERS;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.01.17.
 */
@Getter
public class VoteTestData {
    public static final Comparator<Vote> VOTE_COMPARATOR = new VoteComparator();

    private final Vote vote1;
    private final Set<Vote> votes = new HashSet<>();

    public VoteTestData(PollTestData pollTestData) {
        LunchPlacePoll poll = pollTestData.getPoll2();
        this.vote1 = new Vote(VOTER_ID, poll, poll.getPollItems().iterator().next(), LocalDateTime.now());

        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        PollItem firstPollItem = iterator.next();
        PollItem secondPollItem = iterator.next();
        PollItem [] items = new PollItem[] {firstPollItem, secondPollItem};

        this.votes.add(vote1);
        for (int i = 0; i < VOTERS.size(); i++) {
            this.votes.add(new Vote(VOTERS.get(i).getId(), poll, items[i & 1], LocalDateTime.now().plusMinutes(i*7)));
        }
    }

    private static class VoteComparator implements Comparator<Vote> {

        @Override
        public int compare(Vote v1, Vote v2) {
            return (v1.getPoll().equals(v2.getPoll())
                    && v1.getPollItem().equals(v2.getPollItem())
                    && v1.getVoterId().equals(v2.getVoterId())
                    &&  v1.getVoteTime().equals(v2.getVoteTime())
            ) ? 0 : -1;
        }
    }
}
