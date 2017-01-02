package ua.belozorov.lunchvoting.model.voting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 10.12.16.
 */
public final class PollVoteCollector implements VoteCollector {
    private final List<Vote> votes = new ArrayList<>();
    private final Poll poll;
    private final Collector<Vote, ArrayList<Vote>, PollingResult.ResultEntry> POLL_RESULT_COLLECTOR = Collector.of(
            ArrayList<Vote>::new,
            ArrayList<Vote>::add,
            (current, previous) -> {
                current.addAll(previous);
                return current;
            },
            PollingResult.ResultEntry::new
    );

    PollVoteCollector(Poll poll) {
        this.poll = poll;
    }

    public PollVoteCollector collect(Vote vote) {
        pollCheck(vote);
        votes.add(vote);
        return this;
    }

    public PollVoteCollector collect(List<Vote> votes) {
        votes.forEach(this::pollCheck);
        this.votes.addAll(votes);
        return this;
    }

    private void pollCheck(Vote vote) {
        if (!vote.getPoll().equals(this.poll)) {
            throw new IllegalStateException(String.format(
                    "Detected a vote %s not for this poll %s", vote.getId(), poll.getId()));
        }
    }

    public PollingResult<PollItem> getByPollItem() {
        return result(VoteCollector.pollItemClassifier());
    }

    public <I> PollingResult<I> result(Function<Vote, I> classifier) {
        Map<I, PollingResult.ResultEntry> resultMap = votes.stream()
                .collect(
                        Collectors.groupingBy(
                                classifier,
                                POLL_RESULT_COLLECTOR
                        )
                );
        return new PollingResult<>(resultMap, this.poll);
    }
}
