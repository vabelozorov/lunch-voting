package ua.belozorov.lunchvoting.model.voting.polling;

import org.junit.Assert;
import org.junit.Test;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
public class TimeConstraintTest {

    @Test
    public void testInitWithValidTimeParams() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        new TimeConstraint(start, end, start);
        new TimeConstraint(start, end, start.plusNanos(1));
        new TimeConstraint(start, end, end.minusNanos(1));
    }

    @Test
    public void testInitWithInvalidTimeParams() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        List<IllegalStateException> exceptions = new ArrayList<>();
        try {
            new TimeConstraint(start, end, start.minusNanos(1));
        } catch (IllegalStateException e) {
            exceptions.add(e);
        }
        try {
            new TimeConstraint(start, end, end);
        } catch (IllegalStateException e) {
            exceptions.add(e);
        }
        try {
            new TimeConstraint(start, start, start);
        } catch (IllegalStateException e) {
            exceptions.add(e);
        }
        Assert.assertTrue(exceptions.size() == 3);
    }

    @Test
    public void testisInTimeToChangeVote() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        LocalDateTime changeVoteTimeThreshold = start.plusHours(1);
        TimeConstraint constraint = new TimeConstraint(start, end, changeVoteTimeThreshold);

        assertTrue(constraint.isInTimeToChangeVote(start));
        assertTrue(constraint.isInTimeToChangeVote(start.plusNanos(1)));
        assertTrue(constraint.isInTimeToChangeVote(changeVoteTimeThreshold));

        assertFalse(constraint.isInTimeToChangeVote(start.minusNanos(1)));
        assertFalse(constraint.isInTimeToChangeVote(end));
    }

    @Test
    public void testIsPollActive() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        TimeConstraint constraint = new TimeConstraint(start, end, start.plusHours(1));

        assertTrue(constraint.isPollActive(start));
        assertTrue(constraint.isPollActive(end.minusNanos(1)));

        assertFalse(constraint.isPollActive(start.minusNanos(1)));
        assertFalse(constraint.isPollActive(end));
    }
}