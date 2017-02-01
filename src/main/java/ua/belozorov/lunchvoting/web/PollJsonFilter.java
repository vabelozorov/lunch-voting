package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.02.17.
 */
public class PollJsonFilter implements JsonFilter {
    private final JsonResult json;

    @Autowired
    public PollJsonFilter(JsonResult json) {
        this.json = json;
    }

    @Override
    public void includingFilter(final Object object, RefinedFields fields) {
        Match match = Match.match();
        match.exclude("*");
        fields.get().forEach(match::include);
        json.use(JsonView.with(object)
                .onClass(LunchPlacePoll.class, match)
                .onClass(PollItem.class, Match.match().exclude("version", "poll"))
                .onClass(LunchPlace.class, Match.match().exclude("version"))
                .onClass(Menu.class, Match.match().exclude("version", "lunchPlace"))
        );
    }

    @Override
    public void excludingFilter(final Object object, final List<String> excludeFields) {
        throw new UnsupportedOperationException();
    }
}
