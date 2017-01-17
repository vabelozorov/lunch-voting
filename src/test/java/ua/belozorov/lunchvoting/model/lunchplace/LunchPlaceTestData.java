package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.util.SetToStringConverter;
import ua.belozorov.objtosql.SimpleObjectToSqlConverter;
import ua.belozorov.objtosql.StringSqlColumn;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.testdata.UserTestData.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 22.11.16.
 */
@Getter
public class LunchPlaceTestData {

    public static final Comparator<LunchPlace> LUNCH_PLACE_COMPARATOR = new LunchPlaceComparator();
    private final Resource lunchPlaceSqlResource;

    private LunchPlace place1 = new LunchPlace("FirstPlaceID", 1, "First Place", "First Address", "First Description",
            Collections.singletonList("0501234567"), Collections.emptyList(), ADMIN_ID);
    private LunchPlace place2 = new LunchPlace("SecondPlaceID", 1, "Second Place", "Second Address", "Second Description",
            Arrays.asList("0442345671", "0502345671"), Collections.emptyList(), ADMIN_ID);
    private LunchPlace place3 = new LunchPlace("ThirdPlaceID", 1, "Third Place", "Third Address", "Third Description",
            Collections.singletonList("0503456712"), Collections.emptyList(), GOD_ID);
    private LunchPlace place4 = new LunchPlace("FourthPlaceID", 1, "Fourth Place", "Fourth Address", "Fourth Description",
            Arrays.asList("0444567123", "0444671235", "0504567123", "0934567123"), Collections.emptyList(), GOD_ID);

    private final String place1Id;
    private final String place2Id;
    private final String place3Id;
    private final String place4Id;

    private final Menu menu1 = new Menu(
            LocalDate.now().minusDays(1),
            Arrays.asList(new Dish("Fish", 11.00f, 0), new Dish("Soup", 12.00f, 1), new Dish("Apple Juice", 13.00f, 2)),
            place4
    );
    private final Menu menu2 = new Menu(
            LocalDate.now().minusDays(2),
            Collections.singletonList(new Dish("Potato", 21.00f, 1)),
            place4
    );
    private final Menu menu3 = new Menu(
            LocalDate.now(),
            Collections.singletonList(new Dish("Tomato", 31.00f, 1)),
            place4
    );
    private final Menu menu4 = new Menu(
            LocalDate.now(),
            Collections.singletonList(new Dish("Ice cubes", 41.00f, 1)),
            place4
    );
    private final Menu menu5 = new Menu(
            LocalDate.now(),
            Collections.singletonList(new Dish("Marshmallow", 51.00f, 1)),
            place3
    );
    private final Menu menu6 = new Menu(
            LocalDate.now().minusDays(1),
            Collections.singletonList(new Dish("Green Grass", 61.00f, 1)),
            place3
    );
    private final Menu menu7 = new Menu(
            LocalDate.now().minusDays(2),
            Collections.singletonList(new Dish("Chipotled eggs", 71.00f, 1)),
            place1
    );
    private final Menu menu8 = new Menu(
            LocalDate.now().minusDays(2),
            Collections.singletonList(new Dish("Chipotled chicken", 81.00f, 1)),
            place2
    );

    private final String menu1Id = menu1.getId();
    private final String menu2Id = menu2.getId();
    private final String menu3Id = menu3.getId();
    private final String menu4Id = menu4.getId();
    private final String menu5Id = menu5.getId();
    private final String menu6Id = menu6.getId();
    private final String menu7Id = menu7.getId();
    private final String menu8Id = menu8.getId();

    private final List<LunchPlace> places;
    private final List<Menu> menus;
    private final Resource menuSqlResource;

    public LunchPlaceTestData() {
        this.place1 = this.place1.setMenus(Arrays.asList(menu7));
        this.place2 = this.place2.setMenus(Arrays.asList(menu8));
        this.place3 = this.place3.setMenus(Arrays.asList(menu5, menu6));
        this.place4 = this.place4.setMenus(Arrays.asList(menu1, menu2, menu3, menu4));

        this.place1Id = place1.getId();
        this.place2Id = place2.getId();
        this.place3Id = place3.getId();
        this.place4Id = place4.getId();

        this.places = Arrays.asList(place1, place2, place3, place4);
        Collections.sort(this.places, Comparator.comparing(LunchPlace::getName));

        this.menus = Arrays.asList(menu1, menu2, menu3, menu4, menu5, menu6, menu7, menu8);

        String sql = new SimpleObjectToSqlConverter<LunchPlace>(
          "places",
          Arrays.asList(
              new StringSqlColumn<>("id", LunchPlace::getId),
              new StringSqlColumn<>("name", LunchPlace::getName),
              new StringSqlColumn<>("address", LunchPlace::getAddress),
              new StringSqlColumn<>("description", LunchPlace::getDescription),
              new StringSqlColumn<>("user_id", LunchPlace::getAdminId),
              new StringSqlColumn<>("phones", lp -> new SetToStringConverter().convertToDatabaseColumn(lp.getPhones()))
          )
        ).convert(this.places);
        this.lunchPlaceSqlResource = new ByteArrayResource(sql.getBytes(), "LunchPlaces");
        this.menuSqlResource = new MenuToResourceConverter().convert(this.menus);
    }

    public static LunchPlace getWithFilteredMenu(LocalDate date, LunchPlace place) {
        return place.toBuilder().menus(
                place.getMenus().stream()
                        .filter(m -> m.getEffectiveDate().equals(date))
                        .collect(Collectors.toSet())
        ).build();
    }

    public static List<LunchPlace> getWithFilteredMenu(LocalDate date, LunchPlace... places) {
        return Arrays.stream(places).map(place -> getWithFilteredMenu(date, place)).collect(Collectors.toList());
    }

        public static class LunchPlaceComparator implements Comparator<LunchPlace> {

        @Override
        public int compare(LunchPlace o1, LunchPlace o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getName().equals(o2.getName())
                    && o1.getDescription().equals(o2.getDescription())
                    && new ArrayList<>(o1.getPhones()).equals(new ArrayList<>(o2.getPhones()))
                  &&   o1.getAdminId().equals(o2.getAdminId())
            ) ? 0 : -1;
        }
    }

    public static class WebLunchPlaceComparator implements Comparator<LunchPlace> {

        @Override
        public int compare(LunchPlace o1, LunchPlace o2) {
            return (o1.getId().equals(o2.getId())
                    && o1.getName().equals(o2.getName())
                    && o1.getDescription().equals(o2.getDescription())
                    && new ArrayList<>(o1.getPhones()).equals(new ArrayList<>(o2.getPhones()))
            ) ? 0 : -1;
        }
    }

    public static class MenuToResourceConverter {
        private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private final String DELETE_TABLES = "DELETE FROM dishes;\nDELETE FROM menus;\n\n";
        private final String INSERT_MENU   = "INSERT INTO menus (id, effective_date, place_id) VALUES";
        private final String MENU_ENTRY    = "\n  ('%s', '%s', '%s'),";
        private final String INSERT_DISH   = "INSERT INTO dishes (menu_id, name, price, position) VALUES";
        private final String DISH_ENTRY    = "\n  ('%s', '%s', '%.2f', %d),";
        private final String STATEMENT_END = ";\n\n";

        public Resource convert(Collection<Menu> menus) {
            StringBuilder menuBuilder = new StringBuilder(DELETE_TABLES).append(INSERT_MENU);
            StringBuilder dishBuilder = new StringBuilder(INSERT_DISH);

            for (Menu m : menus) {
                String menuSqlValue = String.format(MENU_ENTRY, m.getId(), m.getEffectiveDate().format(FORMATTER), m.getLunchPlace().getId());
                menuBuilder.append(menuSqlValue);
                for (Dish d : m.getDishes()) {
                    String dishSqlValue = String.format(DISH_ENTRY, m.getId(), d.getName(), d.getPrice(), d.getPosition());
                    dishBuilder.append(dishSqlValue);
                }
            }
            menuBuilder.deleteCharAt(menuBuilder.length() - 1).append(STATEMENT_END);
            dishBuilder.deleteCharAt(dishBuilder.length() - 1).append(STATEMENT_END);
            String sql = menuBuilder.append(dishBuilder).toString();
            return new ByteArrayResource(sql.getBytes(), "Menus & Dishes");
        }
    }
}
