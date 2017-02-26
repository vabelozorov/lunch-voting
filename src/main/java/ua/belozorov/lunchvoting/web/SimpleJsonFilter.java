package ua.belozorov.lunchvoting.web;

import com.monitorjbl.json.JsonResult;
import com.monitorjbl.json.JsonView;
import com.monitorjbl.json.Match;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**

 *
 * Created on 02.02.17.
 */
@Component("simpleJsonFilter")
public class SimpleJsonFilter implements JsonFilter {
    protected final JsonResult json;

    protected SimpleJsonFilter(JsonResult json) {
        this.json = json;
    }

    @Override
    public void includingFilter(Object object, RefinedFields fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void excludingFilter(Object object, Map<Class<?>, Set<String>> excludeMap) {
        JsonView<Object> view = JsonView.with(object);
        for (Map.Entry<Class<?>, Set<String>> me : excludeMap.entrySet()) {
            view = view.onClass(
                    me.getKey(),
                    Match.match().exclude(this.setToArray(me.getValue()))
            );
        }
        json.use(view);
    }

    private String[] setToArray(Set<String> set) {
        return set.toArray(new String[set.size()]);
    }
}
