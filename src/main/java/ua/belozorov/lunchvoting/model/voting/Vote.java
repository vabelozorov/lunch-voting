package ua.belozorov.lunchvoting.model.voting;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 20.11.16.
 */
@Getter
@Entity
@Table(name = "votes")
@Immutable
public final class Vote extends AbstractPersistableObject {

    @NotNull
    @Column(name = "voter_id")
    private final String voterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @NotNull
    private final PollItem pollItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id")
    @NotNull
    private final LunchPlacePoll poll;

    @Column(name = "vote_time")
    @NotNull
    private final LocalDateTime voteTime;

    /**
     * Meant for Spring and JPA
     */
    protected Vote() {
        voterId = null;
        pollItem = null;
        voteTime = null;
        poll = null;
    }

    /**
     * Primary constructor. ID is auto-generated and version is null.
     * @param voterId string value conforming to UUID format representing an ID of a voting subject. Must not be null
     * @param pollItem must not be null
     */
    Vote(String voterId, LunchPlacePoll poll, PollItem pollItem, LocalDateTime voteTime) {
        this(null, null, voterId, poll, pollItem, voteTime);
    }

    /**
     * General constructor. Meant for using with JUnit
     * @param id string value conforming to UUID format
     * @param version must be null to indicate a new object. Non-null value indicates an existing object.
     * @param voterId string value conforming to UUID format representing an ID of a voting subject. Must not be null
     * @param pollItem must not be null
     */
    Vote(String id, Integer version, String voterId, LunchPlacePoll poll, PollItem pollItem, LocalDateTime voteTime) {
        super(id, version);
        this.voterId = Objects.requireNonNull(voterId);
        this.poll = Objects.requireNonNull(poll);
        this.pollItem = Objects.requireNonNull(pollItem);
        this.voteTime = voteTime;
    }
}
