package ua.belozorov.lunchvoting.model.voting;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
public class PollTestData {
    private final LunchPlacePoll poll1;
    private final Resource pollSqlResource;

    private Comparator<LunchPlacePoll> pollComparator = new PollComparator();

    public PollTestData(LunchPlaceTestData placeTestData) {
        LocalDate menuDate = LocalDate.now().minusDays(2);
        LunchPlace place1 = placeTestData.getPlace1();
        LunchPlace place2 = placeTestData.getPlace2();

        place1 = LunchPlaceTestData.getWithFilteredMenu(place1, menuDate);
        place2 = LunchPlaceTestData.getWithFilteredMenu(place2, menuDate);

        this.poll1 = new LunchPlacePoll(
                Arrays.asList(place1, place2),
                menuDate
        );
        this.pollSqlResource = new PollToResourceConverter().convert(Collections.singletonList(poll1));
    }

    public LunchPlacePoll getPoll1() {
        return poll1;
    }

    public Resource getPollSqlResource() {
        return pollSqlResource;
    }

    public Comparator<LunchPlacePoll> getPollComparator() {
        return pollComparator;
    }

    private static class PollComparator implements Comparator<LunchPlacePoll> {
        private static final Comparator<Collection<PollItem>> POLL_ITEMS_COMPARATOR = new PollItemCollectionComparator();
        @Override
        public int compare(LunchPlacePoll o1, LunchPlacePoll o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getPollItems().size() == o2.getPollItems().size()
                    && POLL_ITEMS_COMPARATOR.compare(o1.getPollItems(), o2.getPollItems()) == 0
            ) ? 0 : -1;
        }
    }

    private static class PollItemComparator implements Comparator<PollItem> {
        @Override
        public int compare(PollItem o1, PollItem o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getItem().getId().equals(o2.getItem().getId())
            && o1.getPosition() == o2.getPosition()
            && o1.getPoll().getId().equals(o2.getPoll().getId())) ? 0 : -1;
        }
    }

    private static class PollItemCollectionComparator implements Comparator<Collection<PollItem>> {
        private static final Comparator<PollItem> POLL_ITEM_COMPARATOR = new PollItemComparator();

        @Override
        public int compare(Collection<PollItem> o1, Collection<PollItem> o2) {
            for (PollItem pi1 : o1) {
                for (PollItem pi2 : o2) {
                    if (pi2.equals(pi1) && POLL_ITEM_COMPARATOR.compare(pi1, pi2) != 0) {
                        return -1;
                    }
                }
            }
            return new HashSet<>(o1).equals(new HashSet<>(o2)) ? 0 : -1;
        }
    }

    private static class PollToResourceConverter {
        private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        private static final String DELETE_TABLES = "DELETE FROM polls;\nDELETE FROM poll_items;\n\n";
        private static final String INSERT_POLL = "INSERT INTO polls (id, start_time, end_time, change_time, menuDate) VALUES";
        private static final String POLL_ENTRY = "\n  ('%s', '%s', '%s', '%s', '%s'),";
        private static final String INSERT_POLL_ITEM = "INSERT INTO poll_items (id, poll_id, position, item_id) VALUES";
        private static final String POLL_ITEM_ENTRY = "\n  ('%s', '%s', '%d', '%s'),";
        private static final String STATEMENT_END = ";\n\n";

        Resource convert(Collection<LunchPlacePoll> polls) {
            StringBuilder pollBuilder = new StringBuilder(DELETE_TABLES).append(INSERT_POLL);
            StringBuilder pollItemBuilder = new StringBuilder(INSERT_POLL_ITEM);

            for (LunchPlacePoll p : polls) {
                TimeConstraint timeConstraint = p.getTimeConstraint();
                String pollSqlValue = String.format(POLL_ENTRY,
                        p.getId(),
                        timeConstraint.getStartTime().format(DATE_TIME_FORMATTER),
                        timeConstraint.getEndTime().format(DATE_TIME_FORMATTER),
                        timeConstraint.getVoteChangeThreshold().format(DATE_TIME_FORMATTER),
                        p.getMenuDate().format(DATE_FORMATTER)
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
