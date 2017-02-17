package ua.belozorov.lunchvoting.model.voting;

import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 * @param <I> represents grouping item (key)
 *
 * @author vabelozorov on 29.11.16.
 */
public class PollVoteResult<I> implements VotingResult<I> {
    private final LunchPlacePoll poll;
    private final Map<I, ResultEntry> results;
    private final Collector<Vote, ArrayList<Vote>, ResultEntry> COLLECTOR = Collector.of(
            ArrayList<Vote>::new,
            ArrayList<Vote>::add,
            (current, previous) -> {
                current.addAll(previous);
                return current;
            },
            ResultEntry::new
    );

    public PollVoteResult(LunchPlacePoll poll, Function<Vote, I> classifier) {
        this.poll = poll;
        this.results = Collections.unmodifiableMap(this.toResultEntries(classifier));
    }

    private Map<I, ResultEntry> toResultEntries(Function<Vote, I> classifier) {
        return this.poll.getVotes().stream()
                .collect(
                        Collectors.groupingBy(
                                classifier,
                                COLLECTOR
                        )
                );
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
    public Map<I, List<Vote>> votesForItem() {
        Map<I, List<Vote>> map = new HashMap<>();
        for (Map.Entry<I, ResultEntry> me : results.entrySet()) {
            map.put(me.getKey(), me.getValue().entryVotes);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public List<I> getWinners() {
        Map.Entry<Integer, List<I>> lastEntry = this.countPerItem().entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        TreeMap::new,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toList()))
                ).lastEntry();
        return lastEntry == null ? Collections.emptyList() : lastEntry.getValue();
    }

    @Override
    public List<Vote> votesForItem(I item) {
        return this.votesForItem().entrySet().stream()
                .filter(entry -> entry.getKey().equals(item))
                .findFirst()
                .orElse(null).getValue();
    }

    private final static class ResultEntry {
        private final List<Vote> entryVotes;
        private final int count;

        private ResultEntry(List<Vote> entryVotes) {
            this.entryVotes = Collections.unmodifiableList(entryVotes);
            this.count = entryVotes.size();
        }
    }
}
