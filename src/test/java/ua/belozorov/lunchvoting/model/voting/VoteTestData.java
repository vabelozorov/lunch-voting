package ua.belozorov.lunchvoting.model.voting;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.objtosql.DateTimeSqlColumn;
import ua.belozorov.objtosql.SimpleObjectToSqlConverter;
import ua.belozorov.objtosql.StringSqlColumn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private final Resource voteSqlResource;

    public VoteTestData(PollTestData pollTestData) {
        LunchPlacePoll poll = pollTestData.getPoll2();
        this.vote1 = new Vote(VOTER_ID, poll, poll.getPollItems().iterator().next(), LocalDateTime.now());

        Iterator<PollItem> iterator = poll.getPollItems().iterator();
        PollItem firstPollItem = iterator.next();
        PollItem secondPollItem = iterator.next();
        PollItem[] items = new PollItem[]{firstPollItem, secondPollItem};

        this.votes.add(vote1);
        for (int i = 0; i < VOTERS.size(); i++) {
            this.votes.add(new Vote(VOTERS.get(i).getId(), poll, items[i & 1], LocalDateTime.now().plusMinutes(i * 7)));
        }

        String sql = new SimpleObjectToSqlConverter<>(
                "votes",
                Arrays.asList(
                        new StringSqlColumn<>("id", Vote::getId),
                        new StringSqlColumn<>("voter_id", Vote::getVoterId),
                        new StringSqlColumn<>("poll_id", v -> v.getPoll().getId()),
                        new StringSqlColumn<>("item_id", v -> v.getPollItem().getId()),
                        new DateTimeSqlColumn<>("vote_time", Vote::getVoteTime)
                )
        ).convert(new ArrayList<>(this.votes));

         this.voteSqlResource = new ByteArrayResource(sql.getBytes(), "Votes");
    }

    private static class VoteComparator implements Comparator<Vote> {

        @Override
        public int compare(Vote v1, Vote v2) {
            return (v1.getPoll().equals(v2.getPoll())
                    && v1.getPollItem().equals(v2.getPollItem())
                    && v1.getVoterId().equals(v2.getVoterId())
                    && v1.getVoteTime().equals(v2.getVoteTime())
            ) ? 0 : -1;
        }
    }
}