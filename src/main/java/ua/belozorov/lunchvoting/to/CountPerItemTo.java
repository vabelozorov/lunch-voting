package ua.belozorov.lunchvoting.to;

import ua.belozorov.lunchvoting.model.voting.VotingResult;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 05.02.17.
 */
public final class CountPerItemTo {
    private final String pollId;
    private final List<Map<String, Object>> result;

    public CountPerItemTo(VotingResult<PollItem> pollResult, String pollId ) {
        this.pollId = pollId;
        this.result = pollResult.countPerItem().entrySet().stream()
                .map(me -> this.convertEntryToMap(me.getKey(), me.getValue()))
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertEntryToMap(PollItem item, Integer value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", item.getId());
        map.put("itemId", item.getItemId());
        map.put("count", value);
        return map;
    }
}
