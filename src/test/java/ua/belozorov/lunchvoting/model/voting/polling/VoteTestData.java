package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.objtosql.DateTimeSqlColumn;
import ua.belozorov.objtosql.SimpleObjectToSqlConverter;
import ua.belozorov.objtosql.StringSqlColumn;

import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.01.17.
 */
@Getter
public class VoteTestData {
    public static final Comparator<Vote> VOTE_COMPARATOR = new VoteComparator();

    private final Set<Vote> votesForActivePoll = new HashSet<>();
    private final Resource voteSqlResource;
    private final Set<Vote> votesForPollNoUpdate = new HashSet<>();

    public VoteTestData(PollTestData testPolls) {
        this.votesForActivePoll.addAll(testPolls.getActivePoll().getVotes());
        this.votesForPollNoUpdate.addAll(testPolls.getActivePollNoUpdate().getVotes());

        String sql = this.toSql(this.votesForActivePoll);
        this.voteSqlResource = new ByteArrayResource(sql.getBytes(), "Votes");
    }

    private String toSql(Set<Vote> votes) {
        return new SimpleObjectToSqlConverter<>(
                "votes",
                Arrays.asList(
                        new StringSqlColumn<>("id", Vote::getId),
                        new StringSqlColumn<>("voter_id", Vote::getVoterId),
                        new StringSqlColumn<>("poll_id", v -> v.getPoll().getId()),
                        new StringSqlColumn<>("item_id", v -> v.getPollItem().getId()),
                        new DateTimeSqlColumn<>("vote_time", Vote::getVoteTime)
                )
        ).convert(new ArrayList<>(this.votesForActivePoll));
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