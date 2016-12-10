package ua.belozorov.lunchvoting.testdata;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE3;
import static ua.belozorov.lunchvoting.testdata.LunchPlaceTestData.PLACE4;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.12.16.
 */
public class MenuTestData {
    public static final Menu MENU1 = PLACE4.createMenu(LocalDate.now().minusDays(1), Arrays.asList(
            new Dish("Fish", 11.11f),
            new Dish("Soup", 12.12f),
            new Dish("Apple Juice", 13.11f)
    ));
    public static final Menu MENU2 = PLACE4.createMenu(LocalDate.now(), Collections.singletonList(
            new Dish("Potato", 21.12f)
    ));
    public static final Menu MENU3 = PLACE4.createMenu(LocalDate.now(), Collections.singletonList(
            new Dish("Tomato", 31.13f)
    ));
    public static final Menu MENU4 = PLACE4.createMenu(LocalDate.now(), Collections.singletonList(
            new Dish("Ice cubes", 41.00f)
    ));
    public static final Menu MENU5 = PLACE3.createMenu(LocalDate.now().minusDays(1), Collections.singletonList(
            new Dish("Marshmallow", 51.00f)
    ));
    public static final Menu MENU6 = PLACE3.createMenu(LocalDate.now().minusDays(1),  Collections.singletonList(
            new Dish("Green Grass", 61.00f)
    ));

    public static final Collection<Menu> MENUS = Collections.unmodifiableCollection(
            Arrays.asList(MENU1,MENU2,MENU3,MENU4,MENU5,MENU6)
    );

    public static final String MENU1_ID = MENU1.getId();
    public static final String MENU2_ID = MENU2.getId();
    public static final String MENU3_ID = MENU3.getId();
    public static final String MENU4_ID = MENU4.getId();
    public static final String MENU5_ID = MENU5.getId();
    public static final String MENU6_ID = MENU6.getId();

    public static final Resource MENU_SQL_RESOURCE = new MenuToResourceConverter().convert(MENUS);

    public static class MenuToResourceConverter {
        private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private final String DELETE_TABLES = "DELETE FROM dishes;\nDELETE FROM menus;\n\n";
        private final String INSERT_MENU   = "INSERT INTO menus (id, effective_date, place_id) VALUES";
        private final String MENU_ENTRY    = "\n  ('%s', '%s', '%s'),";
        private final String INSERT_DISH   = "INSERT INTO dishes (menu_id, name, price) VALUES";
        private final String DISH_ENTRY    = "\n  ('%s', '%s', '%.2f'),";
        private final String STATEMENT_END = ";\n\n";

        public Resource convert(Collection<Menu> menus) {
            StringBuilder menuBuilder = new StringBuilder(DELETE_TABLES).append(INSERT_MENU);
            StringBuilder dishBuilder = new StringBuilder(INSERT_DISH);

            for (Menu m : menus) {
                String menuSqlValue = String.format(MENU_ENTRY, m.getId(), m.getEffectiveDate().format(FORMATTER), m.getLunchPlace().getId());
                menuBuilder.append(menuSqlValue);
                for (Dish d : m.getDishes()) {
                    String dishSqlValue = String.format(DISH_ENTRY, m.getId(), d.getName(), d.getPrice());
                    dishBuilder.append(dishSqlValue);
                }
            }
            menuBuilder.deleteCharAt(menuBuilder.length() - 1).append(STATEMENT_END);
            dishBuilder.deleteCharAt(dishBuilder.length() - 1).append(STATEMENT_END);
            String sql = menuBuilder.append(dishBuilder).toString();
            return new ByteArrayResource(sql.getBytes());
        }
    }
}
