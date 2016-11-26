package ua.belozorov.lunchvoting.testdata;

import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static ua.belozorov.lunchvoting.testdata.UserTestData.ADMIN;
import static ua.belozorov.lunchvoting.testdata.UserTestData.GOD;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class LunchPlaceTestData {
    public static final Comparator<LunchPlace> LUNCH_PLACE_COMPARATOR = new LunchPlaceComparator();

    public static LunchPlace PLACE1;
    public static LunchPlace PLACE2;
    public static LunchPlace PLACE3;
    public static LunchPlace PLACE4;
    public static String PLACE1_ID;
    public static String PLACE2_ID;
    public static String PLACE3_ID;
    public static String PLACE4_ID;
    public static Collection<LunchPlace> PLACES;

    static {
        PLACE1 = new LunchPlace("FirstPlaceID", 1, "First Place", "First Address", "First Description",
                Collections.singletonList("0501234567"), Collections.emptyList(), ADMIN);
        PLACE1_ID = PLACE1.getId();

        PLACE2 = new LunchPlace("SecondPlaceID", 1, "Second Place", "Second Address", "Second Description",
                Arrays.asList("0442345671", "0502345671"), Collections.emptyList(), ADMIN);
        PLACE2_ID = PLACE2.getId();

        PLACE3 = new LunchPlace("ThirdPlaceID", 1, "Third Place", "Third Address", "Third Description",
                Collections.singletonList("0503456712"), Collections.emptyList(), GOD);
        PLACE3_ID = PLACE3.getId();

        PLACE4 = new LunchPlace("FourthPlaceID", 1, "Fourth Place", "Fourth Address", "Fourth Description",
                Arrays.asList("0444567123", "0444671235", "0504567123", "0934567123"), Collections.emptyList(), ADMIN);
        PLACE4_ID = PLACE4.getId();

        PLACES = Arrays.asList(PLACE1, PLACE4, PLACE2, PLACE3);
    }

    public static class LunchPlaceComparator implements Comparator<LunchPlace> {

        @Override
        public int compare(LunchPlace o1, LunchPlace o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getName().equals(o2.getName())
                    && o1.getDescription().equals(o2.getDescription())
                    && o1.getPhones().equals(o2.getPhones())
//                  &&   o1.getAdmin().getId().equals(o2.getAdmin().getId())
            ) ? 0 : -1;
        }
    }
}
