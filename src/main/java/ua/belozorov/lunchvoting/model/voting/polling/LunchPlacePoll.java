package ua.belozorov.lunchvoting.model.voting.polling;

import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.exceptions.*;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.AcceptPolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.CommonPolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.VoteForAnotherUpdatePolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.VotePolicy;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * <p>Immutable class that implements a voting functionality for LunchPlace in the application.</p>
 * It processes votes according to modularized, pre-configured policies that implement {@link VotePolicy}
 * interface. It is configured with a start and end time, as well as the time before which a voter can change
 * his/her vote.
 * This implementation does not accepts {@link LunchPlace} objects which have no
 * {@link Menu} objects for a configured {@code menuDate}.
 * <p>
 *     Default set of policies for this implementation prohibits multiple votes for the same poll from the same voter.
 *     Votes are accepted only when the poll is active which is between its start and end time as configured by
 *     {@link TimeConstraint}.
 * </p>
 * <p>
 *     Also policy allow to change the vote before a time threshold as configured by
 *     {@link TimeConstraint}.
 * </p>
 * <p>This class implements {@link Comparable} to support a natural sorting which is by its {@code menuDate},
 * then by ID.</p>
 * <p><b>NOTE:</b>
 * As Hibernate cannot create a proxy for {@code One-To-One} and {@code Many-To-One} final classes,
 * {@code final} modifier here is omitted, yet inheritance of this class in not desired.
 * /p>
 * {@see ua.belozorov.lunchvoting.model.voting.polling.votepolicies.VotePolicy}
 * Created on 29.11.16.
 */
@Entity
@Table(name = "polls")
@Getter(AccessLevel.PUBLIC)
public class LunchPlacePoll extends AbstractPersistableObject implements Comparable<LunchPlacePoll> {

    private final TimeConstraint timeConstraint;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.PERSIST)
    @OrderColumn(name = "position")
    private final List<PollItem> pollItems;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.ALL)
    private final Set<Vote> votes;

    @Column(name = "menu_date")
    private final LocalDate menuDate;

    @Transient
    @Getter(AccessLevel.NONE)
    private final List<VotePolicy> policies;

    /**
     * JPA
     */
    LunchPlacePoll() {
        this.menuDate = null;
        this.timeConstraint = null;
        this.pollItems = new ArrayList<>();
        this.votes = new HashSet<>();
        this.policies = new ArrayList<>();
    }

    /**
     * Constructor that uses a default {@link TimeConstraint} settings.
     *
     * @param lunchPlaces represents items that a voter can vote for. Valid list contains {@link LunchPlace}s with {@link Menu}s
     *                  where {@link Menu#getEffectiveDate()} == {@code menuDate}. This implies that this list cannot be empty
     * @param menuDate date which validates {@link LunchPlace}s through the active date of their menus
     */
    public LunchPlacePoll(List<LunchPlace> lunchPlaces, @Nullable LocalDate menuDate) {
        this(
                TimeConstraint.getDefault(),
                lunchPlaces,
                menuDate
        );
    }

    /**
     * All-args public constructor.
     *
     * @param timeConstraint represents time parameters for this poll
     * @param lunchPlaces represents items that a voter can vote for. Valid list contains {@link LunchPlace}s with {@link Menu}s
     *                  where {@link Menu#getEffectiveDate()} == {@code menuDate}. This implies that this list cannot be empty
     * @param menuDate date which validates {@link LunchPlace}s through the active date of their menus
     */
    public LunchPlacePoll(TimeConstraint timeConstraint, List<LunchPlace> lunchPlaces, @Nullable LocalDate menuDate) {
        ExceptionUtils.requireNonNullNotEmpty(lunchPlaces, () -> new PollException(ErrorCode.NO_POLL_ITEMS));
        ExceptionUtils.checkParamsNotNull(timeConstraint);

        this.menuDate = menuDate == null ? LocalDate.now() : menuDate;
        this.checkMenuDate(lunchPlaces, menuDate);

        this.timeConstraint = timeConstraint;
        this.pollItems = this.convertToPollItems(lunchPlaces);
        this.votes = Collections.emptySet();
        this.policies = this.registerPolicies();
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param timeConstraint represents time parameters for this poll
     * @param pollItems represents item that a voter can vote for. Valid list contains {@link LunchPlace}s with {@link Menu}s
     *                  where {@link Menu#getEffectiveDate()} == {@code menuDate}. This implies that this list cannot be empty
     * @param menuDate date which validates {@link LunchPlace}s through the active date of their menus
     * @param votes
     */
    private LunchPlacePoll(@Nullable String id, @Nullable Integer version, TimeConstraint timeConstraint,
                           List<PollItem> pollItems, LocalDate menuDate, Set<Vote> votes, List<VotePolicy> policies) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, timeConstraint, pollItems, menuDate, votes, policies);

        this.timeConstraint = timeConstraint;
        this.pollItems = pollItems;
        this.menuDate = menuDate;
        this.votes = votes;
        this.policies = policies;
    }

    @PostLoad
    private void init() {
        this.policies.addAll(this.registerPolicies());
    }

    private List<PollItem> convertToPollItems(List<LunchPlace> lunchPlaces) {
        List<PollItem> pollItems = new ArrayList<>();
        for (LunchPlace lunchPlace : lunchPlaces) {
            pollItems.add(new PollItem(lunchPlace, this));
        }
        return pollItems;
    }

    private void checkMenuDate(List<LunchPlace> lunchPlaces, LocalDate menuDate) {
        for(LunchPlace lp : lunchPlaces) {
            if (lp.getMenus().stream()
                .filter(menu -> menu.getEffectiveDate().equals(menuDate))
                .count() == 0 ) {
                throw new PollException(ErrorCode.NO_MENUS_FOR_MENU_DATE);
            }
        }
    }

    private List<VotePolicy> registerPolicies() {
        List<VotePolicy> policies = new ArrayList<>();
        policies.add(new CommonPolicy(this.timeConstraint));
        policies.add(new AcceptPolicy());
        policies.add(new VoteForAnotherUpdatePolicy(this.timeConstraint));
        return policies;
    }

    public LunchPlacePoll withVotes(Set<Vote> votes) {
        return new LunchPlacePoll(id, version, timeConstraint, pollItems, menuDate, Sets.newHashSet(votes), policies);
    }

    public VotePolicyDecision registerVote(String voterId, String pollItemId) {
        Set<Vote> votersVotes = this.votes.stream()
                                            .filter(v -> v.getVoterId().equals(voterId))
                                            .collect(Collectors.toSet());
        VoteIntention intention = new VoteIntentionImpl(voterId, this.pollItemById(pollItemId), votersVotes);

        return this.policies.stream()
                .map(policy -> policy.checkCompliance(intention))
                .filter(vd -> ! vd.isContinue())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("No VotePolicy match. Voter %s, Poll %s, Item %s",
                                voterId, this.id, pollItemId)));
    }

    /**
     * @return unmodifiable {@code List<PollItem>}
     */
    public List<PollItem> getPollItems() {
        return Collections.unmodifiableList(this.pollItems);
    }

    /**
     * @return unmodifiable {@code Set<Vote>}
     */
    public Set<Vote> getVotes() {
        return Collections.unmodifiableSet(this.votes);
    }

    @Override
    public int compareTo(LunchPlacePoll o) {
        int r = this.getMenuDate().compareTo(o.getMenuDate());
        return r != 0 ? (-1)*r : this.getId().compareTo(o.getId());
    }

    @Override
    public String toString() {
        return "LunchPlacePoll{" +
                "id=" + id +
                "timeConstraint=" + timeConstraint +
                ", menuDate=" + menuDate +
                '}';
    }

    private PollItem pollItemById(String id) {
        Objects.requireNonNull(id, "PollItemId must not be null");
        return pollItems.stream()
                .filter(pollItem -> id.equals(pollItem.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Poll %s does not contain item %s", this.getId(), id)));
    }
}