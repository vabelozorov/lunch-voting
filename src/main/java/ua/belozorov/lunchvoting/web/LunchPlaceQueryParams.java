package ua.belozorov.lunchvoting.web;

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
final class LunchPlaceQueryParams {

    @Getter(AccessLevel.NONE)
    private String[] ids = new String[0];

    private LocalDate startDate;

    private LocalDate endDate;

    @Getter(AccessLevel.NONE)
    private String[] fields = new String[0];

    protected LunchPlaceQueryParams() {
    }

    boolean allEmpty() {
        return ids.length == 0 && startDate == null && endDate == null && fields.length == 0;
    }

    boolean onlyIds() {
        return ids.length != 0 && startDate == null && endDate == null && fields.length == 0;
    }

    @NotNull Set<String> getIds() {
        return toSet(ids);
    }

    LocalDate getStartDate() {
        return startDate;
    }

    LocalDate getEndDate() {
        return endDate;
    }

    @NotNull Set<String> getFields() {
        return toSet(fields);
    }

    boolean hasDates() {
        return startDate != null || endDate != null;
    }

    @NotNull Set<String> getFieldsOrDefault(Set<String> defaultFields) {
        return fields.length == 0 ? defaultFields : getFields();
    }

    private Set<String> toSet(String[] strings) {
        return new HashSet<>(Arrays.asList(strings));
    }
}
