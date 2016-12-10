package ua.belozorov.lunchvoting.testdata;

import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.util.*;

import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
public class LunchPlaceTestData {
    public static final Comparator<LunchPlace> LUNCH_PLACE_COMPARATOR = new LunchPlaceComparator();

    public static LunchPlace PLACE1 = new LunchPlace("FirstPlaceID", 1, "First Place", "First Address", "First Description",
            Collections.singletonList("0501234567"), Collections.emptyList(), ADMIN_ID);;
    public static LunchPlace PLACE2 = new LunchPlace("SecondPlaceID", 1, "Second Place", "Second Address", "Second Description",
            Arrays.asList("0442345671", "0502345671"), Collections.emptyList(), ADMIN_ID);
    public static LunchPlace PLACE3 = new LunchPlace("ThirdPlaceID", 1, "Third Place", "Third Address", "Third Description",
            Collections.singletonList("0503456712"), Collections.emptyList(), GOD_ID);
    public static LunchPlace PLACE4 = new LunchPlace("FourthPlaceID", 1, "Fourth Place", "Fourth Address", "Fourth Description",
            Arrays.asList("0444567123", "0444671235", "0504567123", "0934567123"), Collections.emptyList(), GOD_ID);

//    public static Menu MENU1_1 =


    public static String PLACE1_ID = PLACE1.getId();
    public static String PLACE2_ID = PLACE2.getId();
    public static String PLACE3_ID = PLACE3.getId();
    public static String PLACE4_ID = PLACE4.getId();

    public static List<LunchPlace> PLACES = Arrays.asList(PLACE1, PLACE4, PLACE2, PLACE3);

    public static class LunchPlaceComparator implements Comparator<LunchPlace> {

        @Override
        public int compare(LunchPlace o1, LunchPlace o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getName().equals(o2.getName())
                    && o1.getDescription().equals(o2.getDescription())
                    && new ArrayList<String>(o1.getPhones()).equals(new ArrayList<>(o2.getPhones()))
                  &&   o1.getAdminId().equals(o2.getAdminId())
            ) ? 0 : -1;
        }
    }
}
