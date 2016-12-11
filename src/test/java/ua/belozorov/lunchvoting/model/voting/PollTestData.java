package ua.belozorov.lunchvoting.model.voting;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE3;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE4;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
public class PollTestData {
    public static final Poll POLL1 = new Poll(Arrays.asList(PLACE3, PLACE4));
    public static final Resource POLL_SQL_RESOURCE = new PollToResourceConverter().convert(Collections.singletonList(POLL1));

    public static class PollToResourceConverter {
        private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private final String DELETE_TABLES = "DELETE FROM polls;\nDELETE FROM poll_items;\n\n";
        private final String INSERT_POLL = "INSERT INTO polls (id, start_time, end_time, change_time) VALUES";
        private final String POLL_ENTRY = "\n  ('%s', '%s', '%s', '%s'),";
        private final String INSERT_POLL_ITEM = "INSERT INTO poll_items (id, poll_id, position, item_id) VALUES";
        private final String POLL_ITEM_ENTRY = "\n  ('%s', '%s', '%d', '%s'),";
        private final String STATEMENT_END = ";\n\n";

        public Resource convert(Collection<Poll> polls) {
            StringBuilder pollBuilder = new StringBuilder(DELETE_TABLES).append(INSERT_POLL);
            StringBuilder pollItemBuilder = new StringBuilder(INSERT_POLL_ITEM);

            for (Poll p : polls) {
                TimeConstraint timeConstraint = p.getTimeConstraint();
                String pollSqlValue = String.format(POLL_ENTRY,
                        p.getId(),
                        timeConstraint.getStartTime().format(FORMATTER),
                        timeConstraint.getEndTime().format(FORMATTER),
                        timeConstraint.getVoteChangeThreshold().format(FORMATTER)
                );
                pollBuilder.append(pollSqlValue);
                for (PollItem pi : p.getPollItems()) {
                    String pollItemSqlValue = String.format(POLL_ITEM_ENTRY, pi.getId(), p.getId(), pi.getPosition(), pi.getItem().getId());
                    pollItemBuilder.append(pollItemSqlValue);
                }
            }
            pollBuilder.deleteCharAt(pollBuilder.length() - 1).append(STATEMENT_END);
            pollItemBuilder.deleteCharAt(pollItemBuilder.length() - 1).append(STATEMENT_END);
            String sql = pollBuilder.append(pollItemBuilder).toString();
            return new ByteArrayResource(sql.getBytes());
        }
    }
}
