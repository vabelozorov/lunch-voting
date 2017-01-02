package ua.belozorov.lunchvoting.model.voting;

import java.util.*;

/**
 * <h2></h2>
 * @param <I> represents grouping item (key)
 *
 * @author vabelozorov on 29.11.16.
 */
public class PollingResult<I> implements VoteStatistics<I> {
    private final Map<I, ResultEntry> results;
    private final Poll poll;

    PollingResult(Map<I, ResultEntry> results, Poll poll) {
        this.results = Collections.unmodifiableMap(results);
        this.poll = poll;
    }

    @Override
    public Map<I, Integer> countPerItem() {
        Map<I, Integer> map = new HashMap<>();
        for (Map.Entry<I, ResultEntry> me : results.entrySet()) {
            map.put(me.getKey(), me.getValue().count);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public Map<I, List<Vote>> votesPerItem() {
        Map<I, List<Vote>> map = new HashMap<>();
        for (Map.Entry<I, ResultEntry> me : results.entrySet()) {
            map.put(me.getKey(), me.getValue().entryVotes);
        }
        return Collections.unmodifiableMap(map);
    }

    final static class ResultEntry {
        private final List<Vote> entryVotes;
        private final int count;

        ResultEntry(final List<Vote> entryVotes) {
            this.entryVotes = Collections.unmodifiableList(entryVotes);
            this.count = entryVotes.size();
        }
    }
}
