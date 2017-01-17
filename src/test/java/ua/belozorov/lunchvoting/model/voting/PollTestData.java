package ua.belozorov.lunchvoting.model.voting;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.objtosql.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
@Getter
public class PollTestData {
    public static final  Comparator<LunchPlacePoll> POLL_COMPARATOR = new PollComparator();

    private final LunchPlacePoll poll2;
    private final Resource pollSqlResource;
    private final Resource pollItemSqlResource;

    public  PollTestData(LunchPlaceTestData placeTestData) {
        LocalDateTime now = LocalDateTime.now();

        LocalDate menuDate = LocalDate.now().minusDays(2);
        LunchPlace place1 = placeTestData.getPlace1();
        LunchPlace place2 = placeTestData.getPlace2();

        place1 = LunchPlaceTestData.getWithFilteredMenu(menuDate, place1);
        place2 = LunchPlaceTestData.getWithFilteredMenu(menuDate, place2);

        this.poll2 = new LunchPlacePoll(
                now.minusHours(2),
                now.plusHours(2),
                now.plusMinutes(10),
                Arrays.asList(place1, place2),
                menuDate
        );

        String pollSql = new SimpleObjectToSqlConverter<>(
                "polls",
                Arrays.asList(
                        new StringSqlColumn<>("id", LunchPlacePoll::getId),
                        new DateTimeSqlColumn<>("start_time", lpp -> lpp.getTimeConstraint().getStartTime()),
                        new DateTimeSqlColumn<>("end_time", lpp -> lpp.getTimeConstraint().getEndTime()),
                        new DateTimeSqlColumn<>("change_time", lpp -> lpp.getTimeConstraint().getVoteChangeThreshold()),
                        new DateSqlColumn<>("menu_date", LunchPlacePoll::getMenuDate)
                )
        ).convert(Collections.singletonList(poll2));
        this.pollSqlResource = new ByteArrayResource(pollSql.getBytes(), "Polls");

        String pollItemsSql = new SimpleObjectToSqlConverter<>(
                "poll_items",
                Arrays.asList(
                        new StringSqlColumn<>("id", PollItem::getId),
                        new StringSqlColumn<>("poll_id", pi -> pi.getPoll().getId()),
                        new IntegerSqlColumn<>("position", PollItem::getPosition),
                        new StringSqlColumn<>("item_id",pi -> pi.getItem().getId())
                )
        ).convert(
                Arrays.asList(poll2).stream()
                    .flatMap(p -> p.getPollItems().stream())
                    .collect(Collectors.toList())
        );
        this.pollItemSqlResource = new ByteArrayResource(pollItemsSql.getBytes(), "PollItems");

    }

    public PollItem getPOll2PollItem1() {
        return this.poll2.getPollItems().iterator().next();
    }
    public PollItem getPOll2PollItem2() {
        Iterator<PollItem> iterator = this.poll2.getPollItems().iterator();
        iterator.next();
        return iterator.next();
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
}
