package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.matching.EqualsComparator;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData;
import ua.belozorov.objtosql.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE;
import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE_TIME;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;
import static ua.belozorov.lunchvoting.model.UserTestData.A1_VOTERS;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER_ID;

/**

 *
 * Created on 11.12.16.
 */
@Getter
public class PollTestData {
    public static final PollComparator POLL_COMPARATOR = new PollComparator();
    public static final PollComparator POLL_COMPARATOR_NO_ASSOC = new PollComparator(false, false);

    private final LunchPlacePoll activePoll;
    private final LunchPlacePoll pastPoll;
    private final LunchPlacePoll futurePoll;
    private final LunchPlacePoll activePollNoUpdate;
    private final LunchPlacePoll a2Poll1;
    private final Resource pollSqlResource;
    private final Resource pollItemSqlResource;

    @Getter(AccessLevel.NONE)
    private final Map<String, Integer> positionMap = new HashMap<>();
    private final List<LunchPlacePoll> a1Polls = new ArrayList<>();

    public  PollTestData(LunchPlaceTestData placeTestData) {
        this.pastPoll = this.createPastPoll(placeTestData);
        this.activePoll = this.createActivePoll(placeTestData);
        this.activePollNoUpdate = this.createActivePollNoUpdate(placeTestData);
        this.futurePoll = this.createFuturePoll(placeTestData);
        this.a2Poll1 = this.createA2Poll1(placeTestData);

        String sql = this.createA1PollSqlResource(this.pastPoll, this.activePoll,
                this.activePollNoUpdate, this.futurePoll) + this.createA2PollSqlResource(this.a2Poll1);
        this.pollSqlResource = new ByteArrayResource(sql.getBytes(), "Polls");
        this.pollItemSqlResource = this.createPollItemSqlResource(this.pastPoll, this.activePoll,
                this.activePollNoUpdate, this.futurePoll, this.a2Poll1);

        this.a1Polls.add(this.futurePoll);
        this.a1Polls.add(this.activePoll);
        this.a1Polls.add(this.activePollNoUpdate);
        this.a1Polls.add(this.pastPoll);
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

        TimeConstraint timeConstraint = new TimeConstraint(
                NOW_DATE_TIME.minusDays(2).minusHours(2),
                NOW_DATE_TIME.minusDays(2).plusHours(2),
                NOW_DATE_TIME.minusDays(2).plusMinutes(10)
        );
        LunchPlacePoll poll = new LunchPlacePoll(timeConstraint,
                Arrays.asList(
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDatePast, placeTestData.getPlace2())
                ),
                menuDatePast
        );
        PollItem[] items = new PollItem[]{poll.getPollItems().get(0), poll.getPollItems().get(1)};
        Set<Vote> votes = new HashSet<>();
        for (int i = 0; i < A1_VOTERS.size(); i++) {
            votes.add(new Vote(A1_VOTERS.get(i).getId(), poll, items[i & 1])
                            .withVoteTime(NOW_DATE_TIME.minusDays(2).plusSeconds(10*i)));
        }
        return poll.withVotes(votes);
    }

    private LunchPlacePoll createActivePoll(LunchPlaceTestData placeTestData) {
        LocalDate menuDateToday = NOW_DATE;
        TimeConstraint timeConstraint = new TimeConstraint(NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.plusMinutes(10)
        );
        LunchPlacePoll poll =  new LunchPlacePoll(timeConstraint,
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        PollItem[] items = new PollItem[]{poll.getPollItems().get(0), poll.getPollItems().get(1)};
        Set<Vote> votes = new HashSet<>();
        for (int i = 0; i < A1_VOTERS.size(); i++) {
            votes.add(new Vote(A1_VOTERS.get(i).getId(), poll, items[i & 1])
                            .withVoteTime(NOW_DATE_TIME.minusHours(1).plusSeconds(10*i)));
        }
        return poll.withVotes(votes);
    }

    private LunchPlacePoll createActivePollNoUpdate(LunchPlaceTestData placeTestData) {
        LocalDate menuDateToday = NOW_DATE;
        TimeConstraint timeConstraint = new TimeConstraint(NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.minusMinutes(10));
        LunchPlacePoll poll = new LunchPlacePoll(timeConstraint,
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace3()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getPlace4())
                ),
                menuDateToday
        );
        Vote vote = new Vote(VOTER_ID, poll, poll.getPollItems().get(0));
        return poll.withVotes(Collections.singleton(vote));
    }

    private LunchPlacePoll createFuturePoll(LunchPlaceTestData placeTestData) {
        LocalDate menuDateFuture = NOW_DATE.plusDays(2);
        TimeConstraint timeConstraint = new TimeConstraint(
                NOW_DATE_TIME.plusDays(1).minusHours(2),
                NOW_DATE_TIME.plusDays(1).plusHours(2),
                NOW_DATE_TIME.plusDays(1).plusMinutes(10)
        );
        return new LunchPlacePoll(timeConstraint,
                Arrays.asList(
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace1()),
                        getWithFilteredMenu(menuDateFuture, placeTestData.getPlace2())
                ),
                menuDateFuture
        );
    }

    private LunchPlacePoll createA2Poll1(LunchPlaceTestData placeTestData) {
        LocalDate menuDateToday = NOW_DATE;
        TimeConstraint timeConstraint = new TimeConstraint(
                NOW_DATE_TIME.minusHours(2),
                NOW_DATE_TIME.plusHours(2),
                NOW_DATE_TIME.plusMinutes(10)
        );
        return new LunchPlacePoll(timeConstraint,
                Arrays.asList(
                        getWithFilteredMenu(menuDateToday, placeTestData.getArea2place1()),
                        getWithFilteredMenu(menuDateToday, placeTestData.getArea2place2())
                ),
                menuDateToday
        );
    }

    private String createA1PollSqlResource(LunchPlacePoll... polls) {
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
        return pollSql;
    }

    private String createA2PollSqlResource(LunchPlacePoll... polls) {
        String pollSql = new SimpleObjectToSqlConverter<>(
                "polls",
                Arrays.asList(
                        new StringSqlColumn<>("id", LunchPlacePoll::getId),
                        new StringSqlColumn<>("area_id", (lp) -> "AREA2_ID"),
                        new DateTimeSqlColumn<>("start_time", lpp -> lpp.getTimeConstraint().getStartTime()),
                        new DateTimeSqlColumn<>("end_time", lpp -> lpp.getTimeConstraint().getEndTime()),
                        new DateTimeSqlColumn<>("change_time", lpp -> lpp.getTimeConstraint().getVoteChangeThreshold()),
                        new DateSqlColumn<>("menu_date", LunchPlacePoll::getMenuDate)
                )
        ).convert(Arrays.asList(polls));
        return pollSql;
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

    public static class PollComparator implements EqualsComparator<LunchPlacePoll> {
        private static final EqualsComparator<List<PollItem>> POLL_ITEMS_COMPARATOR = new PollItemCollectionComparator();
        private final boolean needCompareItems;
        private final boolean needCompareVotes;

        public PollComparator() {
            this.needCompareItems = true;
            this.needCompareVotes = true;
        }

        public PollComparator(boolean needCompareItems, boolean needCompareVotes) {
            this.needCompareItems = needCompareItems;
            this.needCompareVotes = needCompareVotes;
        }

        public PollComparator noItems() {
            return new PollComparator(false, this.needCompareVotes);
        }

        public PollComparator noVotes() {
            return new PollComparator(this.needCompareItems, false);
        }

        @Override
        public boolean compare(LunchPlacePoll obj, LunchPlacePoll another) {
            boolean result = obj.getId().equals(another.getId())
                    && obj.getTimeConstraint() == obj.getTimeConstraint();
            if (result && needCompareItems) {
                result = obj.getPollItems().size() == another.getPollItems().size()
                        && POLL_ITEMS_COMPARATOR.compare(obj.getPollItems(), another.getPollItems());
            }
            if (result && needCompareVotes) {
                result = obj.getVotes().equals(another.getVotes()); // Comparing only by Vote#id
            }
            return result;
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
