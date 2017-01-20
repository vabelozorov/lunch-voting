package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.objtosql.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE;
import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE_TIME;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTERS;
import static ua.belozorov.lunchvoting.testdata.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
@Getter
public class PollTestData {
    public static final  Comparator<LunchPlacePoll> POLL_COMPARATOR = new PollComparator();

    private LunchPlacePoll activePoll;
    private final Resource pollSqlResource;
    private final Resource pollItemSqlResource;
    private final LunchPlacePoll pastPoll;
    private final LunchPlacePoll futurePoll;
    private LunchPlacePoll activePollNoUpdate;

    public  PollTestData(LunchPlaceTestData placeTestData) {
        LocalDate menuDatePast = NOW_DATE.minusDays(2);
        LocalDate menuDateToday = NOW_DATE;
        LocalDate menuDateFuture = NOW_DATE.plusDays(2);

        this.activePollNoUpdate = new LunchPlacePoll(
                NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.minusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        this.pastPoll = new LunchPlacePoll(
                NOW_DATE_TIME.minusDays(2).minusHours(2),
                NOW_DATE_TIME.minusDays(2).plusHours(2),
                NOW_DATE_TIME.minusDays(2).plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace2())
                ),
                menuDatePast
        );
        this.futurePoll = new LunchPlacePoll(
                NOW_DATE_TIME.plusDays(1).minusHours(2),
                NOW_DATE_TIME.plusDays(1).plusHours(2),
                NOW_DATE_TIME.plusDays(1).plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace2())
                ),
                menuDateFuture
        );
        this.activePoll = new LunchPlacePoll(
                NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        PollItem[] items = new PollItem[]{this.getActivePollPollItem1(), this.getActivePollPollItem2()};
        Set<Vote> votes = new HashSet<>();
        for (int i = 0; i < VOTERS.size(); i++) {
            votes.add(activePoll.registerVote(VOTERS.get(i).getId(), items[i & 1].getId()).getAcceptedVote());
            try {Thread.sleep(10);} catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.activePoll = this.activePoll.toBuilder().votes(votes).build();

        Vote vote = this.activePollNoUpdate.registerVote(VOTER_ID, this.activePollNoUpdate.getPollItems().iterator().next().getId()).getAcceptedVote();
        this.activePollNoUpdate = this.activePollNoUpdate.toBuilder().votes(Collections.singleton(vote)).build();

        String pollSql = new SimpleObjectToSqlConverter<>(
                "polls",
                Arrays.asList(
                        new StringSqlColumn<>("id", LunchPlacePoll::getId),
                        new DateTimeSqlColumn<>("start_time", lpp -> lpp.getTimeConstraint().getStartTime()),
                        new DateTimeSqlColumn<>("end_time", lpp -> lpp.getTimeConstraint().getEndTime()),
                        new DateTimeSqlColumn<>("change_time", lpp -> lpp.getTimeConstraint().getVoteChangeThreshold()),
                        new DateSqlColumn<>("menu_date", LunchPlacePoll::getMenuDate)
                )
        ).convert(Arrays.asList(pastPoll, activePoll, futurePoll));
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
                Stream.of(activePoll, pastPoll, futurePoll)
                    .flatMap(p -> p.getPollItems().stream())
                    .collect(Collectors.toList())
        );
        this.pollItemSqlResource = new ByteArrayResource(pollItemsSql.getBytes(), "PollItems");
    }

    public PollItem getActivePollPollItem1() {
        return this.activePoll.getPollItems().iterator().next();
    }

    public PollItem getActivePollPollItem2() {
        Iterator<PollItem> iterator = this.activePoll.getPollItems().iterator();
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
