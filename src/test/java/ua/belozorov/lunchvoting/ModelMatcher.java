package ua.belozorov.lunchvoting;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Comparator;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 17.11.16.
 */
public class ModelMatcher<T> extends BaseMatcher<T>{
    private final static EqualsComparator DEFAULT_COMPARATOR = (o1, o2) -> String.valueOf(o1).equals(String.valueOf(o2));
    private EqualsComparator<T> comparator;
    private T expected;

    public ModelMatcher(EqualsComparator<T> comparator, T expected) {
        Objects.requireNonNull(comparator);
        Objects.requireNonNull(expected);

        this.comparator = comparator;
        this.expected = expected;
    }

    @SuppressWarnings("unchecked")
    public ModelMatcher(T expected) {
        this(DEFAULT_COMPARATOR, expected);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(Object actual) {
        return comparator.compare(this.expected, (T) actual);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(this.expected.toString());
    }
}
