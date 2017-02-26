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

 *
 * Created on 14.01.17.
 */
@Getter
public class VoteTestData {
    public static final VoteComparator VOTE_COMPARATOR = new VoteComparator();

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

    public static class VoteComparator implements EqualsComparator<Vote> {
        private final boolean withAssoc;

        public VoteComparator() {
            this.withAssoc = true;
        }

        private VoteComparator(boolean withAssoc) {
            this.withAssoc = withAssoc;
        }

        public VoteComparator noAssoc() {
            return new VoteComparator(false);
        }

        @Override
        public boolean compare(Vote obj, Vote another) {
            return this.withAssoc ? this.fullCompare(obj, another) : this.simpleCompare(obj, another);
        }

        private boolean fullCompare(Vote obj, Vote another) {
            return  obj.getId().equals(another.getId())
                    && obj.getPoll().equals(another.getPoll())
                    && obj.getPollItem().equals(another.getPollItem())
                    && obj.getVoterId().equals(another.getVoterId())
                    && obj.getVoteTime().equals(another.getVoteTime());
        }

        private boolean simpleCompare(Vote obj, Vote another) {
            return  obj.getId().equals(another.getId())
                    && obj.getVoterId().equals(another.getVoterId())
                    && obj.getVoteTime().equals(another.getVoteTime());
        }
    }
}