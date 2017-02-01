package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.12.16.
 */
@Component
class LunchPlaceJsonFilter implements JsonFilter {
    private final JsonResult json;

    @Autowired
    LunchPlaceJsonFilter(JsonResult json) {
        this.json = json;
    }

    @Override
    public void excludingFilter(Object object, List<String> excludeFields) {
        Match match = Match.match();
        excludeFields.forEach(match::exclude);
        json.use(JsonView.with(object).onClass(LunchPlace.class, match));
    }

    @Override
    public void includingFilter(Object object, RefinedFields fields) {
        Match match = Match.match();
        match.exclude("*");
        fields.get().forEach(match::include);
        json.use(JsonView.with(object)
                .onClass(LunchPlace.class, match)
                .onClass(Menu.class, Match.match()
                        .exclude("version"))
        );
    }
}
