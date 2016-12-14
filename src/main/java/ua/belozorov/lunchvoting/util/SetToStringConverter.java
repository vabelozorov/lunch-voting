package ua.belozorov.lunchvoting.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.12.16.
 */
public class SetToStringConverter implements AttributeConverter<Set<String>, String> {
    @Override
    public String convertToDatabaseColumn(final Set<String> attribute) {
        return String.join(",", attribute);
    }

    @Override
    public Set<String> convertToEntityAttribute(final String dbData) {
        return new HashSet<String>(Arrays.asList(dbData.split(",")));
    }
}
