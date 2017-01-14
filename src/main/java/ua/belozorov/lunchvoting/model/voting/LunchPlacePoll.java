package ua.belozorov.lunchvoting.model.voting;

import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.*;
import ua.belozorov.lunchvoting.exceptions.MultipleVoteException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.exceptions.PollNotActiveException;
import ua.belozorov.lunchvoting.exceptions.VoteChangeNotAllowedException;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
@Entity
@Table(name = "polls")
@Immutable
@Getter(AccessLevel.PACKAGE)
public final class LunchPlacePoll extends AbstractPersistableObject implements Poll {
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(12, 0);
    private static final LocalTime DEFAULT_ALLOW_CHANGE_VOTE_TIME = LocalTime.of(11, 0);

    private final TimeConstraint timeConstraint;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.PERSIST)
    @OrderBy("position ASC")
    private final Set<PollItem> pollItems;

    @Getter
    private final LocalDate menuDate;

    @Transient
    private final Collection<Consumer<VoteIntention>> pollValidators;

    @Transient
    private final Collection<Consumer<VoteIntention>> voteChangeValidators;

    /**
     * Meant for Spring and JPA
     */
    protected LunchPlacePoll() {
        this.menuDate = null;
        this.timeConstraint = null;
        this.pollItems = null;
        this.pollValidators = getPollValidators();
        this.voteChangeValidators = getVoteChangeValidators();
    }

    public LunchPlacePoll(List<LunchPlace> lunchPlaces, LocalDate menuDate) {
        this(
                LocalDateTime.of(LocalDate.now(), DEFAULT_START_TIME),
                LocalDateTime.of(LocalDate.now(), DEFAULT_END_TIME),
                LocalDateTime.of(LocalDate.now(), DEFAULT_ALLOW_CHANGE_VOTE_TIME),
                lunchPlaces,
                menuDate
        );
    }

        /**
         * Primary constructor
         * @param startTime time from which the poll starts to accept votes.
         * @param endTime starting from this time votes will be rejected.
         * @param voteChangeTimeThreshold time until which (included) a voter is allowed to change his/her mind.
         * @param lunchPlaces items to vote for. If null, the empty list is assumed.
         */
    public LunchPlacePoll(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime voteChangeTimeThreshold, List<LunchPlace> lunchPlaces, LocalDate menuDate) {
        this.checkLunchPlaceDate(lunchPlaces, menuDate);
        this.menuDate = menuDate;
        this.timeConstraint = new TimeConstraint(startTime, endTime, voteChangeTimeThreshold);
        this.pollItems = convertToPollItems(lunchPlaces);
        this.pollValidators = getPollValidators();
        this.voteChangeValidators = getVoteChangeValidators();
    }

    private Set<PollItem> convertToPollItems(List<LunchPlace> lunchPlaces) {
        if (lunchPlaces == null) {
            lunchPlaces = Collections.emptyList();
        }
        List<PollItem> pollItems = new ArrayList<>();
        for (int i = 0; i < lunchPlaces.size(); i++) {
            pollItems.add(new PollItem(i, lunchPlaces.get(i), this));
        }
        return new LinkedHashSet<>(pollItems);
    }

    private void checkLunchPlaceDate(List<LunchPlace> places, LocalDate date) {
        for (LunchPlace place : places) {
            if (place.getMenus().isEmpty()) {
                throw new IllegalStateException("LunchPlace without Menu");
            } else {
                for (Menu m : place.getMenus()) {
                    if ( ! m.getEffectiveDate().equals(date)) {
                        throw new IllegalStateException("LunchPlace instance has a menu with an unexpected date");
                    }
                }
            }
        }
    }

    private Collection<Consumer<VoteIntention>> getPollValidators() {
        Collection<Consumer<VoteIntention>> validators = new ArrayList<>();
        validators.add(this::belongsToMe);
        validators.add(this::hasPollItem);
        validators.add(this::isRunning);
        validators.add(this::oneVote);
        return Collections.unmodifiableCollection(validators);
    }

    private Collection<Consumer<VoteIntention>> getVoteChangeValidators() {
        Collection<Consumer<VoteIntention>> validators = new ArrayList<>();
        validators.add(this::voteChange);
        return Collections.unmodifiableCollection(validators);
    }

    public VoteDecision verify(VoteIntention intention) {
        pollValidators.forEach(validator -> validator.accept(intention));
        if (intention.isForAnotherItem()) {
            voteChangeValidators.forEach(validator -> validator.accept(intention));
            Vote vote = new Vote(intention.getVoterId(), this, this.pollItemById(intention.getPollItemId()), intention.getMadeTime());
            return VoteDecision.update(vote);
        } else if ( ! intention.hasVotedEarlier()) {
            Vote vote = new Vote(intention.getVoterId(), this, this.pollItemById(intention.getPollItemId()), intention.getMadeTime());
            return VoteDecision.accept(vote);
        } else {
            throw new IllegalStateException("Unexpected state during vote verification");
        }
    }

    private void oneVote(VoteIntention intention) {
        if (intention.isForTheSameItem()) {
            throw new MultipleVoteException(intention);
        }
    }

    private void voteChange(VoteIntention intention) {
        LocalDateTime voteTime = intention.getMadeTime();
        if ( ! this.timeConstraint.isInTimeToChangeVote(voteTime)) {
            throw new VoteChangeNotAllowedException(intention);
        }
    }

    private void isRunning(VoteIntention intention) {
        LocalDateTime voteTime = intention.getMadeTime();
        if ( ! this.timeConstraint.isPollActive(voteTime)) {
            throw new PollNotActiveException(intention);
        }
    }

    private void belongsToMe(VoteIntention intention) {
        String intentionPollId = intention.getPollId();
        if ( ! intentionPollId.equals(this.getId())) {
            throw new IllegalStateException(String.format("Wrong pollId %s: mine is %s", intentionPollId, this.getId()));
        }
    }

    private void hasPollItem(VoteIntention intention) {
        String intentionPollItemId = intention.getPollItemId();
        this.pollItems.stream()
                .map(PollItem::getId)
                .filter(intentionPollItemId::equals)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(intentionPollItemId, PollItem.class));
    }

    PollItem pollItemById(String id) {
        Objects.requireNonNull(id, "PollItemId must not be null");
        return pollItems.stream()
                .filter(pollItem -> id.equals(pollItem.getId()))
                .findFirst().orElseThrow(() -> new IllegalStateException(String.format("Poll %s does not contain item %s", this.getId(), id)));
    }
//
//    public PollingResult getPollResult(Set<Vote> votes) {
//        Set<String> pollItemIds = pollItems.stream().map(PollItem::getId).collect(Collectors.toSet());
//        PollingResult.VoteCollector collector = PollingResult.getCollector(this.getId());
//        for (Vote vote : votes) {
//            if (vote.getPoll().equals(this.getId()) && pollItemIds.contains(vote.getPollItem())) {
//                collector.collect(vote);
//            } else {
//                throw new IllegalStateException(
//                        String.format("Unexpected vote with pollId %s and pollItemId %s. My pollItemId is %s",
//                                vote.getId(), vote.getPollItem(), this.getId())
//                );
//            }
//        }
//        return collector.getByPollItem();
//    }

    public Set<PollItem> getPollItems() {
        return this.pollItems;
    }


}
