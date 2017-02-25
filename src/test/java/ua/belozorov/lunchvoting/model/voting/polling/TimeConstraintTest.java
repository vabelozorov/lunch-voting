package ua.belozorov.lunchvoting.model.voting.polling;

import org.junit.Assert;
import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.exceptions.PollException;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * <h2></h2>
 *
 * Created on 09.12.16.
 */
public class TimeConstraintTest extends AbstractTest{

    @Test
    public void testInitWithValidTimeParams() throws Exception {
        LocalDateTime start = NOW_DATE_TIME;
        LocalDateTime end = start.plusHours(2);
        // all values the same
        new TimeConstraint(start, start, start);
        // threshold equals start
        new TimeConstraint(start, end, start);
        //threshold between start and end
        new TimeConstraint(start, end, start.plusSeconds(1));
        // threshold equals end
        new TimeConstraint(start, end, end);
    }

    @Test
    public void testInitWithInvalidTimeParams() throws Exception {
        super.assertExceptionCount(PollException.class,
                // end before start
                () -> new TimeConstraint(NOW_DATE_TIME, NOW_DATE_TIME.minusSeconds(1), NOW_DATE_TIME.plusHours(1)),
                // threshold before start
                () -> new TimeConstraint(NOW_DATE_TIME, NOW_DATE_TIME.plusHours(1), NOW_DATE_TIME.minusSeconds(1)),
                // threshold after end
                () -> new TimeConstraint(NOW_DATE_TIME, NOW_DATE_TIME.plusHours(1), NOW_DATE_TIME.plusHours(1).plusSeconds(1))
        );
    }

    @Test
    public void isInTimeToChangeVote() throws Exception {
        LocalDateTime start = NOW_DATE_TIME;
        LocalDateTime end = start.plusHours(2);
        LocalDateTime changeVoteTimeThreshold = start.plusHours(1);
        TimeConstraint constraint = new TimeConstraint(start, end, changeVoteTimeThreshold);

        assertTrue(constraint.isInTimeToChangeVote(start));
        assertTrue(constraint.isInTimeToChangeVote(start.plusSeconds(1)));
        assertTrue(constraint.isInTimeToChangeVote(changeVoteTimeThreshold));

        assertFalse(constraint.isInTimeToChangeVote(start.minusSeconds(1)));
        assertFalse(constraint.isInTimeToChangeVote(end));
    }

    @Test
    public void testIsPollActive() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        TimeConstraint constraint = new TimeConstraint(start, end, start.plusHours(1));

        assertTrue(constraint.isPollActive(start));
        assertTrue(constraint.isPollActive(end.minusSeconds(1)));

        assertFalse(constraint.isPollActive(start.minusSeconds(1)));
        assertFalse(constraint.isPollActive(end));
    }
}