package ua.belozorov.lunchvoting.web.queries;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.12.16.
 */
@Setter(AccessLevel.PUBLIC)
public final class LunchPlaceQueryParams {

    @Getter(AccessLevel.NONE)
    private String[] ids = new String[0];

    private LocalDate startDate;

    private LocalDate endDate;

    @Getter(AccessLevel.NONE)
    private String[] fields = new String[0];

    protected LunchPlaceQueryParams() {
    }

    @NotNull public  Set<String> getIds() {
        return toSet(ids);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @NotNull public Set<String> getFields() {
        return toSet(fields);
    }

    public boolean hasDates() {
        return startDate != null || endDate != null;
    }

    private Set<String> toSet(String[] strings) {
        return ImmutableSet.copyOf(strings);
    }
}
