package ua.belozorov.lunchvoting;

import org.hamcrest.Matcher;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.to.UserTo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class MatcherUtils {

    public static <T> List<Matcher<? super T>> matchCollection(Collection<T> expected, EqualsComparator<T> comparator) {
        return expected.stream()
                .map(entity -> new ModelMatcher<>(comparator, entity))
                .collect(Collectors.toList());
    }

    public static <T> Matcher<T> matchByToString(T expected) {
        return new ModelMatcher<>(expected);
    }

    public static <T> Matcher<T> matchSingle(T expected, EqualsComparator<T> comparator) {
        return new ModelMatcher<>(comparator, expected);
    }

    public static void assertSql(int selects, int inserts, int updates, int deletes) {
        assertSelectCount(selects);
        assertInsertCount(inserts);
        assertUpdateCount(updates);
        assertDeleteCount(deletes);
    }

    public static void assertSelect(int count) {
        assertSql(count, 0, 0, 0);
    }

    public static void assertInsert(int count) {
        assertSql(0, count, 0, 0);
    }

    public static void assertUpdate(int count) {
        assertSql(0, 0, count, 0);
    }

    public static void assertDelete(int count) {
        assertSql(0, 0, 0, count);
    }
}
