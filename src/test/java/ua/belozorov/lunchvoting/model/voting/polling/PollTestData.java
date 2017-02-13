package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.EqualsComparator;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.objtosql.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE;
import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE_TIME;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTERS;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 11.12.16.
 */
@Getter
public class PollTestData {
    public static final EqualsComparator<Poll> POLL_COMPARATOR = new PollComparator();

    private final LunchPlacePoll activePoll;
    private final Resource pollSqlResource;
    private final Resource pollItemSqlResource;
    private final LunchPlacePoll pastPoll;
    private final LunchPlacePoll futurePoll;
    private final LunchPlacePoll activePollNoUpdate;
    private final Map<String, Integer> positionMap = new HashMap<>();
    private final Set<LunchPlacePoll> allPolls = new LinkedHashSet<>();

    public  PollTestData(LunchPlaceTestData placeTestData) {
        this.pastPoll = this.createPastPoll(placeTestData);
        this.activePoll = this.createActivePoll(placeTestData);
        this.activePollNoUpdate = this.createActivePollNoUpdate(placeTestData);
        this.futurePoll = this.createFuturePoll(placeTestData);

        this.pollSqlResource = this.createPollSqlResource(this.pastPoll, this.activePoll,
                this.activePollNoUpdate, this.futurePoll);
        this.pollItemSqlResource = this.createPollItemSqlResource(this.pastPoll, this.activePoll,
                this.activePollNoUpdate, this.futurePoll);

        this.allPolls.add(this.futurePoll);
        this.allPolls.add(this.activePoll);
        this.allPolls.add(this.activePollNoUpdate);
        this.allPolls.add(this.pastPoll);
    }


    public PollItem getActivePollPollItem1() {
        return this.activePoll.getPollItems().get(0);
    }

    public PollItem getActivePollPollItem2() {
        return this.activePoll.getPollItems().get(1);
    }

    public PollItem getPastPollPollItem1() {
        return this.pastPoll.getPollItems().get(0);
    }

    public PollItem getPastPollPollItem2() {
        return this.pastPoll.getPollItems().get(1);
    }

    private Integer getPollItemPosition(PollItem pi) {
        String id = pi.getPoll().getId();
        return positionMap.merge(id, 0, (o, n) -> ++o);
    }

    private LunchPlacePoll createPastPoll(LunchPlaceTestData placeTestData) {
        LocalDate menuDatePast = NOW_DATE.minusDays(2);

        LunchPlacePoll poll = new LunchPlacePoll(
                NOW_DATE_TIME.minusDays(2).minusHours(2),
                NOW_DATE_TIME.minusDays(2).plusHours(2),
                NOW_DATE_TIME.minusDays(2).plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace2())
                ),
                menuDatePast
        );
        PollItem[] items = new PollItem[]{poll.getPollItems().get(0), poll.getPollItems().get(1)};
        Set<Vote> votes = new HashSet<>();
        for (int i = 0; i < VOTERS.size(); i++) {
            votes.add(new Vote(VOTERS.get(i).getId(), poll, items[i & 1], NOW_DATE_TIME.minusDays(2).plusSeconds(10*i)));
        }
        return poll.toBuilder().votes(votes).build();
    }

    private LunchPlacePoll createActivePoll(LunchPlaceTestData placeTestData) {
        LocalDate menuDateToday = NOW_DATE;
        LunchPlacePoll poll =  new LunchPlacePoll(
                NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        PollItem[] items = new PollItem[]{poll.getPollItems().get(0), poll.getPollItems().get(1)};
        Set<Vote> votes = new HashSet<>();
        for (int i = 0; i < VOTERS.size(); i++) {
            votes.add(new Vote(VOTERS.get(i).getId(), poll, items[i & 1], NOW_DATE_TIME.minusHours(1).plusSeconds(10*i)));
        }
        return poll.toBuilder().votes(votes).build();
    }

    private LunchPlacePoll createActivePollNoUpdate(LunchPlaceTestData placeTestData) {
        LocalDate menuDateToday = NOW_DATE;
        LunchPlacePoll poll = new LunchPlacePoll(
                NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.minusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        Vote vote = new Vote(VOTER_ID, poll, poll.getPollItems().get(0), NOW_DATE_TIME);
        return poll.toBuilder().votes(Collections.singleton(vote)).build();
    }

    private LunchPlacePoll createFuturePoll(LunchPlaceTestData placeTestData) {
        LocalDate menuDateFuture = NOW_DATE.plusDays(2);
        return new LunchPlacePoll(
                NOW_DATE_TIME.plusDays(1).minusHours(2),
                NOW_DATE_TIME.plusDays(1).plusHours(2),
                NOW_DATE_TIME.plusDays(1).plusMinutes(10),
                Arrays.asList(
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace2())
                ),
                menuDateFuture
        );
    }

    private Resource createPollSqlResource(LunchPlacePoll... polls) {
        String pollSql = new SimpleObjectToSqlConverter<>(
                "polls",
                Arrays.asList(
                        new StringSqlColumn<>("id", LunchPlacePoll::getId),
                        new StringSqlColumn<>("area_id", (lp) -> "AREA1_ID"),
                        new DateTimeSqlColumn<>("start_time", lpp -> lpp.getTimeConstraint().getStartTime()),
                        new DateTimeSqlColumn<>("end_time", lpp -> lpp.getTimeConstraint().getEndTime()),
                        new DateTimeSqlColumn<>("change_time", lpp -> lpp.getTimeConstraint().getVoteChangeThreshold()),
                        new DateSqlColumn<>("menu_date", LunchPlacePoll::getMenuDate)
                )
        ).convert(Arrays.asList(polls));
        return new ByteArrayResource(pollSql.getBytes(), "Polls");
    }

    private Resource createPollItemSqlResource(LunchPlacePoll... polls) {
        String pollItemsSql = new SimpleObjectToSqlConverter<>(
                "poll_items",
                Arrays.asList(
                        new StringSqlColumn<>("id", PollItem::getId),
                        new StringSqlColumn<>("poll_id", pi -> pi.getPoll().getId()),
                        new IntegerSqlColumn<>("position", this::getPollItemPosition),
                        new StringSqlColumn<>("item_id", PollItem::getItemId)
                )
        ).convert(
                Stream.of(polls)
                        .flatMap(p -> p.getPollItems().stream())
                        .collect(Collectors.toList())
        );
        return new ByteArrayResource(pollItemsSql.getBytes(), "PollItems");
    }

    private static class PollComparator implements EqualsComparator<Poll> {
        private static final EqualsComparator<List<PollItem>> POLL_ITEMS_COMPARATOR = new PollItemCollectionComparator();
        @Override
        public boolean compare(Poll obj, Poll another) {
            return obj.getId().equals(another.getId())
                    && obj.getPollItems().size() == another.getPollItems().size()
                    && POLL_ITEMS_COMPARATOR.compare(obj.getPollItems(), another.getPollItems());
        }
    }

    private static class PollItemComparator implements EqualsComparator<PollItem> {
        @Override
        public boolean compare(PollItem obj, PollItem another) {
            return (obj.getId().equals(another.getId())
                    && obj.getItemId().equals(another.getItemId())
                    && obj.getPoll().equals(another.getPoll()));
        }
    }

    private static class PollItemCollectionComparator implements EqualsComparator<List<PollItem>> {
        private static final EqualsComparator<PollItem> POLL_ITEM_COMPARATOR = new PollItemComparator();

        @Override
        public boolean compare(List<PollItem> obj, List<PollItem> another) {
            if (obj.size() != another.size()) return false;
            for (int i = 0; i < obj.size(); i++) {
                if ( ! POLL_ITEM_COMPARATOR.compare(obj.get(i), another.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}
