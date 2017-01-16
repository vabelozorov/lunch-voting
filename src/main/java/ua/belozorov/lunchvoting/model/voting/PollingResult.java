package ua.belozorov.lunchvoting.model.voting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 * @param <I> represents grouping item (key)
 *
 * @author vabelozorov on 29.11.16.
 */
public class PollingResult<I> implements VoteStatistics<I> {
    private final Map<I, ResultEntry> results;
    private final LunchPlacePoll poll;
    private Map<I, Integer> countPerItemMap;
    private Map<I, List<Vote>> votesPerItemMap;
    private List<I> winners;

    PollingResult(Map<I, ResultEntry> results, LunchPlacePoll poll) {
        this.results = Collections.unmodifiableMap(results);
        this.poll = poll;
    }

    @Override
    public Map<I, Integer> countPerItem() {
        if (this.countPerItemMap == null) {
            Map<I, Integer> map = new HashMap<>();
            for (Map.Entry<I, ResultEntry> me : results.entrySet()) {
                map.put(me.getKey(), me.getValue().count);
            }
            this.countPerItemMap = Collections.unmodifiableMap(map);
        }
        return this.countPerItemMap;
    }

    @Override
    public Map<I, List<Vote>> votesPerItem() {
        if (this.votesPerItemMap == null) {
            Map<I, List<Vote>> map = new HashMap<>();
            for (Map.Entry<I, ResultEntry> me : results.entrySet()) {
                map.put(me.getKey(), me.getValue().entryVotes);
            }
            this.votesPerItemMap = Collections.unmodifiableMap(map);
        }
        return this.votesPerItemMap;
    }

    @Override
    public List<I> getWinners() {
        if (this.winners == null) {
            Map.Entry<Integer, List<I>> lastEntry = this.countPerItem().entrySet().stream()
                    .collect(Collectors.groupingBy(
                            Map.Entry::getValue,
                            TreeMap::new,
                            Collectors.mapping(Map.Entry::getKey, Collectors.toList()))).lastEntry();
            this.winners = lastEntry == null ? null : lastEntry.getValue();
        }
        return winners;
    }

    @Override
    public List<Vote> votesForItem(I item) {
        return this.votesPerItem().entrySet().stream()
                .filter(entry -> entry.getKey().equals(item))
                .findFirst()
                .orElse(null).getValue();
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
