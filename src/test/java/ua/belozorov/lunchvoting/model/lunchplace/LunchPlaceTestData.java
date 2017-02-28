package ua.belozorov.lunchvoting.model.lunchplace;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ua.belozorov.lunchvoting.EqualsComparator;
import ua.belozorov.lunchvoting.util.hibernate.PhonesToStringConverter;
import ua.belozorov.objtosql.SimpleObjectToSqlConverter;
import ua.belozorov.objtosql.StringSqlColumn;
import ua.belozorov.objtosql.ToSqlConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.belozorov.lunchvoting.AbstractTest.NOW_DATE;

/**

 *
 * Created on 22.11.16.
 */
@Getter
public class LunchPlaceTestData {

    public static final EqualsComparator<LunchPlace> LUNCH_PLACE_COMPARATOR = new LunchPlaceComparator();
    public static final EqualsComparator<Menu> MENU_COMPARATOR = new MenuComparator();
    public static final EqualsComparator<Menu> MENU_WITH_DISHES_COMPARATOR = new MenuWithDishesComparator();
    private final Resource lunchPlaceSqlResource;

    private LunchPlace place1 = new LunchPlace("FirstPlaceID",  "First Place", "First Address", "First Description",
            Collections.singleton("0501234567"));
    private LunchPlace place2 = new LunchPlace("SecondPlaceID", "Second Place", "Second Address", "Second Description",
            ImmutableSet.of("0442345671", "0502345671"));
    private LunchPlace place3 = new LunchPlace("ThirdPlaceID", "Third Place", "Third Address", "Third Description",
           Collections.singleton("0503456712"));
    private LunchPlace place4 = new LunchPlace("FourthPlaceID", "Fourth Place", "Fourth Address", "Fourth Description",
            ImmutableSet.of("0444567123", "0444671235", "0504567123", "0934567123"));

    private LunchPlace area2place1 = new LunchPlace("AREA2_PL1_ID", "AREA2_PL_NAME1", "First Address", "First Description",
            Collections.singleton("0501234567"));
    private LunchPlace area2place2 = new LunchPlace("AREA2_PL2_ID",  "AREA2_PL_NAME2", "Second Address", "Second Description",
            ImmutableSet.of("0442345671", "0502345671"));

    private final String place1Id;
    private final String place2Id;
    private final String place3Id;
    private final String place4Id;
    private final String a2place1Id;
    private final String a2place2Id;

    private final Menu menu1 = new Menu(
            NOW_DATE.minusDays(1),
            ImmutableSet.of(new Dish("Fish", 11.00f, 0), new Dish("Soup", 12.00f, 1), new Dish("Apple Juice", 13.00f, 2)),
            place4
    );
    private final Menu menu2 = new Menu(
            NOW_DATE.minusDays(2),
            ImmutableSet.of(new Dish("Potato", 21.00f, 0), new Dish("Kartoha", 21.00f, 1)),
            place4
    );
    private final Menu menu3 = new Menu(
            NOW_DATE,
            ImmutableSet.of(new Dish("Tomato", 31.00f, 0), new Dish("Pamidor", 31.00f, 1)),
            place4
    );
    private final Menu menu4 = new Menu(
            NOW_DATE,
            ImmutableSet.of(new Dish("Ice cubes", 41.00f, 0), new Dish("Lyodik", 41.00f, 1)),
            place4
    );
    private final Menu menu5 = new Menu(
            NOW_DATE,
            ImmutableSet.of(new Dish("Marshmallow", 51.00f, 0), new Dish("Zhevachka", 51.00f, 1)),
            place3
    );
    private final Menu menu6 = new Menu(
            NOW_DATE.minusDays(1),
            ImmutableSet.of(new Dish("Green Grass", 61.00f, 0), new Dish("Travka", 61.00f, 1)),
            place3
    );
    private final Menu menu7 = new Menu(
            NOW_DATE.minusDays(2),
            ImmutableSet.of(new Dish("Chipotled eggs", 71.00f, 0), new Dish("Jajca bobo", 71.00f, 1)),
            place1
    );
    private final Menu menu8 = new Menu(
            NOW_DATE.plusDays(2),
            ImmutableSet.of(new Dish("Chipotled chicken", 81.00f, 0), new Dish("Cypa bobo", 81.00f, 1)),
            place1
    );
    private final Menu menu9 = new Menu(
            NOW_DATE.minusDays(2),
            ImmutableSet.of(new Dish("Cucumber salad", 81.00f, 0), new Dish("Agurchiki", 81.00f, 1)),
            place2
    );
    private final Menu menu10 = new Menu(
            NOW_DATE.plusDays(2),
            ImmutableSet.of(new Dish("Coconut shell", 81.00f, 0), new Dish("Nesjedobno", 81.00f, 1)),
            place2
    );
    private final Menu menu11 = new Menu(
            NOW_DATE,
            ImmutableSet.of(new Dish("Pepper mint", 81.00f, 0), new Dish("Haladok", 81.00f, 1)),
            area2place1
    );
    private final Menu menu12 = new Menu(
            NOW_DATE,
            ImmutableSet.of(new Dish("Seaweed", 81.00f, 0), new Dish("Travka-sol", 81.00f, 1)),
            area2place2
    );

    private final String menu1Id = menu1.getId();
    private final String menu2Id = menu2.getId();
    private final String menu3Id = menu3.getId();
    private final String menu4Id = menu4.getId();
    private final String menu5Id = menu5.getId();
    private final String menu6Id = menu6.getId();
    private final String menu7Id = menu7.getId();
    private final String menu8Id = menu8.getId();

    private final List<LunchPlace> a1Places;
    private final List<LunchPlace> a2Places;
    private final List<Menu> allMenus;
    private final Resource menuSqlResource;

    public LunchPlaceTestData() {
        this.place1 = this.place1.withMenus(ImmutableSet.of(menu7, menu8));
        this.place2 = this.place2.withMenus(ImmutableSet.of(menu9, menu10));
        this.place3 = this.place3.withMenus(ImmutableSet.of(menu5, menu6));
        this.place4 = this.place4.withMenus(ImmutableSet.of(menu1, menu2, menu3, menu4));
        this.area2place1 = this.area2place1.withMenus(ImmutableSet.of(menu11));
        this.area2place2 = this.area2place2.withMenus(ImmutableSet.of(menu12));

        this.place1Id = place1.getId();
        this.place2Id = place2.getId();
        this.place3Id = place3.getId();
        this.place4Id = place4.getId();
        this.a2place1Id = area2place1.getId();
        this.a2place2Id = area2place2.getId();

        this.a1Places = Stream.of(place1, place2, place3, place4).sorted().collect(Collectors.toList());
        this.a2Places = Stream.of(area2place1, area2place2).sorted().collect(Collectors.toList());


        this.allMenus = Arrays.asList(menu1, menu2, menu3, menu4, menu5, menu6, menu7, menu8, menu9, menu10, menu11, menu12);

        String a1PlaceSql = new SimpleObjectToSqlConverter<>(
          "places",
          Arrays.asList(
              new StringSqlColumn<>("id", LunchPlace::getId),
              new StringSqlColumn<>("area_id", (lp) -> "AREA1_ID"),
              new StringSqlColumn<>("name", LunchPlace::getName),
              new StringSqlColumn<>("address", LunchPlace::getAddress),
              new StringSqlColumn<>("description", LunchPlace::getDescription),
              new StringSqlColumn<>("phones", lp -> new PhonesToStringConverter().convertToDatabaseColumn(lp.getPhones()))
          )
        ).convert(a1Places);
        String a2PlaceSql = new SimpleObjectToSqlConverter<>(
                "places",
                Arrays.asList(
                        new StringSqlColumn<>("id", LunchPlace::getId),
                        new StringSqlColumn<>("area_id", (lp) -> "AREA2_ID"),
                        new StringSqlColumn<>("name", LunchPlace::getName),
                        new StringSqlColumn<>("address", LunchPlace::getAddress),
                        new StringSqlColumn<>("description", LunchPlace::getDescription),
                        new StringSqlColumn<>("phones", lp -> new PhonesToStringConverter().convertToDatabaseColumn(lp.getPhones()))
                )
        ).convert(a2Places);
        this.lunchPlaceSqlResource = new ByteArrayResource((a1PlaceSql+a2PlaceSql).getBytes(), "LunchPlaces");
        this.menuSqlResource = new ByteArrayResource(new MenuToResourceConverter().convert(this.allMenus).getBytes(), "Menus & Dishes");
    }

    public List<LunchPlace> getA1Places() {
        return this.a1Places.stream().sorted().collect(Collectors.toList());
    }

    public List<LunchPlace> getA2Places() {
        return this.a2Places.stream().sorted().collect(Collectors.toList());
    }

    public static LunchPlace getWithFilteredMenu(LocalDate date, LunchPlace place) {
        return place.withMenus(
                place.getMenus().stream()
                        .filter(m -> m.getEffectiveDate().equals(date))
                        .collect(Collectors.toSet())
        );
    }

    public static List<LunchPlace> getWithFilteredMenu(LocalDate date, LunchPlace... places) {
        return Arrays.stream(places).map(place -> getWithFilteredMenu(date, place)).collect(Collectors.toList());
    }

    public static class LunchPlaceComparator implements EqualsComparator<LunchPlace> {

        @Override
        public boolean compare(LunchPlace obj, LunchPlace another) {
            return obj.getId().equals(another.getId())
                    && Objects.equals(obj.getName(),another.getName())
                    && Objects.equals(obj.getDescription(), another.getDescription())
                    && new ArrayList<>(obj.getPhones()).equals(new ArrayList<>(another.getPhones()));
        }
    }

    public static class WebLunchPlaceComparator implements EqualsComparator<LunchPlace> {

        @Override
        public boolean compare(LunchPlace obj, LunchPlace another) {
            return obj.getId().equals(another.getId())
                    && obj.getName().equals(another.getName())
                    && obj.getDescription().equals(another.getDescription())
                    && new ArrayList<>(obj.getPhones()).equals(new ArrayList<>(another.getPhones()));
        }
    }

    public static class MenuToResourceConverter implements ToSqlConverter<Menu> {
        private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private final String DELETE_TABLES = "DELETE FROM dishes;\nDELETE FROM menus;\n\n";
        private final String INSERT_MENU   = "INSERT INTO menus (id, effective_date, place_id) VALUES";
        private final String MENU_ENTRY    = "\n  ('%s', '%s', '%s'),";
        private final String INSERT_DISH   = "INSERT INTO dishes (menu_id, name, price, position) VALUES";
        private final String DISH_ENTRY    = "\n  ('%s', '%s', '%.2f', %d),";
        private final String STATEMENT_END = ";\n\n";

        @Override
        public ToSqlConverter<Menu> clearDbTable(boolean b) {
            throw new UnsupportedOperationException();
        }

        public String convert(List<Menu> menus) {
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
            return menuBuilder.append(dishBuilder).toString();
        }
    }

    private static class MenuComparator implements EqualsComparator<Menu> {
        @Override
        public boolean compare(Menu obj, Menu another) {
            return obj.getId().equals(another.getId())
                    && obj.getEffectiveDate().equals(another.getEffectiveDate());
        }
    }

    private static class MenuWithDishesComparator implements EqualsComparator<Menu> {
        @Override
        public boolean compare(Menu obj, Menu another) {
            return obj.getId().equals(another.getId())
                    && obj.getEffectiveDate().equals(another.getEffectiveDate())
                    && obj.getDishes().equals(obj.getDishes());
        }
    }
}
