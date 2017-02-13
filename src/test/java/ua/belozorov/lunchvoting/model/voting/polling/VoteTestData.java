package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.EqualsComparator;
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
    public static final EqualsComparator<Vote> VOTE_COMPARATOR = new VoteComparator();

    private final Set<Vote> votesForActivePoll = new HashSet<>();
    private final Set<Vote> votesForPollNoUpdate = new HashSet<>();
    private final Set<Vote> votesForPastPoll = new HashSet<>();
    private final Resource voteSqlResource;

    public VoteTestData(PollTestData testPolls) {
        this.votesForActivePoll.addAll(testPolls.getActivePoll().getVotes());
        this.votesForPollNoUpdate.addAll(testPolls.getActivePollNoUpdate().getVotes());
        this.votesForPastPoll.addAll(testPolls.getPastPoll().getVotes());

        String sql = this.toSql(this.votesForActivePoll);
        this.voteSqlResource = new ByteArrayResource(sql.getBytes(), "Votes");
    }

    private String toSql(Set<Vote> votes) {
        List<Vote> forConversion = new ArrayList<>();
        forConversion.addAll(this.votesForActivePoll);
        forConversion.addAll(this.votesForPastPoll);
        return new SimpleObjectToSqlConverter<>(
                "votes",
                Arrays.asList(
                        new StringSqlColumn<>("id", Vote::getId),
                        new StringSqlColumn<>("voter_id", Vote::getVoterId),
                        new StringSqlColumn<>("poll_id", v -> v.getPoll().getId()),
                        new StringSqlColumn<>("item_id", v -> v.getPollItem().getId()),
                        new DateTimeSqlColumn<>("vote_time", Vote::getVoteTime)
                )
        ).convert(forConversion);
    }

    private static class VoteComparator implements EqualsComparator<Vote> {

        @Override
        public boolean compare(Vote obj, Vote another) {
            return  obj.getPoll().equals(another.getPoll())
                    && obj.getPollItem().equals(another.getPollItem())
                    && obj.getVoterId().equals(another.getVoterId())
                    && obj.getVoteTime().equals(another.getVoteTime());
        }
    }
}