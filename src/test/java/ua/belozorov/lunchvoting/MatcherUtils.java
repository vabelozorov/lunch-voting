package ua.belozorov.lunchvoting;

import org.hamcrest.Matcher;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.to.UserTo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class MatcherUtils {

    public static <T> List<Matcher<? super T>> matchCollection(Collection<T> expected, Comparator<T> comparator) {
        return expected.stream()
                .map(entity -> new ModelMatcher<>(comparator, entity))
                .collect(Collectors.toList());
    }

    public static <T> Matcher<T> matchByToString(T expected) {
        return new ModelMatcher<>(expected);
    }

    public static <T> Matcher<T> matchSingle(T user, Comparator<T> comparator) {
        return new ModelMatcher<>(comparator, user);
    }
}
