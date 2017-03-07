package ua.belozorov.lunchvoting.matching;

import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;

/**

 *
 * Created on 22.11.16.
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
