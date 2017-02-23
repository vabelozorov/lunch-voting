package ua.belozorov.lunchvoting.web.queries;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.Null;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2>A value object that aggregates query parameters for LunchPlaceController</h2>
 *
 * @author vabelozorov on 29.12.16.
 */
@Setter
public final class LunchPlaceQueryParams {

    /*
        IDs of LunchPlace instances to query.
        No restriction on ID by default
     */
    private String[] ids;

    /*
        Fields in a LunchPlace instance that should be encoded in JSON response
        It is id and name by default
     */
    private String[] fields;

    /*
        Start of a date range to which LunchPlace's Menu#effectiveDate must belong to
     */
    private LocalDate startDate;

    /*
        End of a date range to which LunchPlace's Menu#effectiveDate must belong to
     */
    private LocalDate endDate;

    /*
        Spring
     */
    protected LunchPlaceQueryParams() {
        ids = new String[0];
        fields = new String[0];
        startDate = null;
        endDate = null;
    }

    public  Set<String> getIds() {
        return toSet(ids);
    }

    public Set<String> getFields() {
        return toSet(fields);
    }

    @Nullable public LocalDate getStartDate() {
        return startDate;
    }

    @Nullable public LocalDate getEndDate() {
        return endDate;
    }

    public boolean hasDates() {
        return startDate != null || endDate != null;
    }

    private Set<String> toSet(String[] strings) {
        return ImmutableSet.copyOf(strings);
    }
}
