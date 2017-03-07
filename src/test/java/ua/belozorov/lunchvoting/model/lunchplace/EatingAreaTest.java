package ua.belozorov.lunchvoting.model.lunchplace;

import org.junit.Test;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.model.area.EatingArea;

import static org.junit.Assert.*;
import static ua.belozorov.lunchvoting.model.UserTestData.ALIEN_USER1;

/**

 *
 * Created on 10.02.17.
 */
public class EatingAreaTest extends AbstractTest {

    @Test
    public void testJoin() throws Exception {
        EatingArea area = testAreas.getFirstArea();
        EatingArea updated = area.addMember(ALIEN_USER1.assignAreaId(area.getId()));
        assertTrue(updated.getVoters().size() == 8);
    }

    @Test(expected = IllegalStateException.class)
    public void userCannotJoinIfAreaIdNull() throws Exception {
        EatingArea area = testAreas.getFirstArea();
        area.addMember(ALIEN_USER1);
    }

    @Test(expected = IllegalStateException.class)
    public void userCannotJoinIfAreaIdNotAssigned() throws Exception {
        EatingArea area = testAreas.getFirstArea();
        area.addMember(ALIEN_USER1.assignAreaId("xxx"));
    }
}