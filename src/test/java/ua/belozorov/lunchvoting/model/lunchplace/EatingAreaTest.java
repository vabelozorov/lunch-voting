package ua.belozorov.lunchvoting.model.lunchplace;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;

import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.model.UserTestData.ALIEN_USER1;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.02.17.
 */
public class EatingAreaTest extends AbstractTest {

    @Test
    public void testJoin() throws Exception {
        EatingArea area = testAreas.getFirstArea();
        JoinAreaRequest request = new JoinAreaRequest(ALIEN_USER1, area);
        EatingArea updated = area.join(request);
        assertTrue(updated.getUsers().size() == 8);
    }
}