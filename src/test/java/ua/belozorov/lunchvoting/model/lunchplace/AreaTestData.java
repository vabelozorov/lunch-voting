package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.EqualsComparator;
import ua.belozorov.lunchvoting.MatcherUtils;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollTestData;
import ua.belozorov.lunchvoting.to.AreaTo;
import ua.belozorov.lunchvoting.to.JoinRequestTo;
import ua.belozorov.objtosql.DateTimeSqlColumn;
import ua.belozorov.objtosql.SimpleObjectToSqlConverter;
import ua.belozorov.objtosql.StringSqlColumn;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.MatcherUtils.equalsWithNulls;
import static ua.belozorov.lunchvoting.model.UserTestData.ALL_USERS;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.02.17.
 */
@Getter
public final class AreaTestData {
    public final static EqualsComparator<EatingArea> AREA_COMPARATOR = new EatingAreaComparator();
    public final static EqualsComparator<JoinAreaRequest> REQUEST_COMPARATOR = new JoinRequestComparator();
    public final static EqualsComparator<AreaTo> AREA_TO_COMPARATOR = new AreaToComparator();

    private final EatingArea firstArea;
    private final Resource areaSqlResource;
    private final String firstAreaName;
    private final String firstAreaId;
    private final LocalDateTime firstAreaDate;

    public AreaTestData(PollTestData pollTestData, LunchPlaceTestData placeTestData) {
        this.firstArea = new EatingArea("AREA1_ID", "AREA_NAME", GOD).toBuilder()
                .places(new HashSet<>(placeTestData.getPlaces()))
                .polls(pollTestData.getAllPolls())
                .users(ALL_USERS)
                .build();
        this.firstAreaName = firstArea.getName();
        this.firstAreaId = firstArea.getId();
        this.firstAreaDate = firstArea.getCreated();

        String areas = new SimpleObjectToSqlConverter<>("areas", Arrays.asList(
            new StringSqlColumn<>("id", EatingArea::getId),
            new StringSqlColumn<>("name", EatingArea::getName),
            new DateTimeSqlColumn<>("created", EatingArea::getCreated)
        )).convert(Arrays.asList(this.firstArea));
        this.areaSqlResource = new ByteArrayResource(areas.getBytes(), "Areas");
    }

    public static AreaTo dto(EatingArea area) {
        return new AreaTo(area.getId(), area.getName(), area.getCreated(),
            area.getUsers().stream().map(User::getId).sorted().collect(Collectors.toList()),
            area.getPolls().stream().map(Poll::getId).sorted().collect(Collectors.toList()),
            area.getPlaces().stream().map(LunchPlace::getId).sorted().collect(Collectors.toList())
        );
    }

    public static AreaTo dtoSummary(EatingArea area) {
        return new AreaTo(area.getId(), area.getName(), area.getCreated(),
                area.getUsers().stream().map(User::getId).count(),
                area.getPolls().stream().map(Poll::getId).count(),
                area.getPlaces().stream().map(LunchPlace::getId).count()
        );
    }

    private static class EatingAreaComparator implements EqualsComparator<EatingArea> {
        @Override
        public boolean compare(EatingArea obj, EatingArea another) {
            return obj.getId().equals(another.getId())
                    && obj.getCreated().equals(another.getCreated())
                    && obj.getName().equals(another.getName());
        }
    }

    private static class JoinRequestComparator implements EqualsComparator<JoinAreaRequest> {

        @Override
        public boolean compare(JoinAreaRequest obj, JoinAreaRequest another) {
            return obj.getCreated().equals(another.getCreated())
                    && obj.getId().equals(another.getId())
                    && obj.getRequester().equals(another.getRequester())
                    && obj.getStatus().equals(another.getStatus())
                    && equalsWithNulls(obj.getDecidedOn(), another.getDecidedOn())
                    && obj.getArea().equals(another.getArea());
        }
    }

    private static class AreaToComparator implements EqualsComparator<AreaTo> {

        @Override
        public boolean compare(AreaTo obj, AreaTo another) {
            return obj.getId().equals(another.getId())
                    && obj.getName().equals(another.getName())
                    && obj.getCreated().equals(another.getCreated())
                    && MatcherUtils.equalsWithNulls(obj.getPlaceCount(), another.getPlaceCount())
                    && MatcherUtils.equalsWithNulls(obj.getPlaceIds(), another.getPlaceIds())
                    && MatcherUtils.equalsWithNulls(obj.getPollCount(), another.getPollCount())
                    && MatcherUtils.equalsWithNulls(obj.getPolls(), another.getPolls())
                    && MatcherUtils.equalsWithNulls(obj.getUserCount(), another.getUserCount())
                    && MatcherUtils.equalsWithNulls(obj.getUsers(), another.getUsers());
        }
    }
}
