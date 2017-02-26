package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Optional.ofNullable;
import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * <p>Immutable class that stores information about an accepted vote made by a voter.</p>
 * <p>This class implements {@link Comparable} to support a natural sorting which is by its {@code voteTime},
 * then by ID.</p>
 *
 * Created on 20.11.16.
 */
@Getter
@Entity
@Table(name = "votes")
@Immutable
public final class Vote extends AbstractPersistableObject implements Comparable<Vote> {

    @Column(name = "voter_id")
    private final String voterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private final PollItem pollItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    private final LunchPlacePoll poll;

    @Column(name = "vote_time")
    private final LocalDateTime voteTime;

    /**
     * JPA
     */
    Vote() {
        voterId = null;
        pollItem = null;
        voteTime = null;
        poll = null;
    }

    /**
     * @param voterId
     * @param poll
     * @param pollItem
     */
    Vote(String voterId, LunchPlacePoll poll, PollItem pollItem) {
        this(null, null, voterId, poll, pollItem, LocalDateTime.now());
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param voterId
     * @param poll
     * @param pollItem
     * @param voteTime
     */
    private Vote(String id, Integer version, String voterId, LunchPlacePoll poll, PollItem pollItem, LocalDateTime voteTime) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, voterId, poll, pollItem, voteTime);

        this.voterId = voterId;
        this.poll = poll;
        this.pollItem = pollItem;
        this.voteTime = voteTime;
    }

    Vote withVoteTime(LocalDateTime voteTime) {
        return new Vote(this.id, this.version, this.voterId, this.poll, this.pollItem, voteTime);
    }

    @Override
    public int compareTo(Vote o) {
        int i = this.voteTime.compareTo(o.voteTime);
        return i != 0 ? i : this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "voterId='" + voterId + '\'' +
                "voteTime='" +voteTime + '\'' +
                '}';
    }
}
