package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Builder;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
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
    private final List<VotePolicy> policies;

    /**
     * Meant for Spring and JPA
     */
    protected LunchPlacePoll() {
        this.menuDate = null;
        this.timeConstraint = null;
        this.pollItems = null;
        this.votes = null;
        this.policies = new ArrayList<>();
    }

    public LunchPlacePoll(List<LunchPlace> lunchPlaces, @Nullable LocalDate menuDate) {
        this(
                TimeConstraint.getDefault(),
                lunchPlaces,
                menuDate
        );
    }

    /**
     * Primary constructor

     * @param lunchPlaces non-null, non-empty list of items to vote for
     */
    public LunchPlacePoll(TimeConstraint timeConstraint, List<LunchPlace> lunchPlaces, @Nullable LocalDate menuDate) {
        ExceptionUtils.requireNonNullNotEmpty(lunchPlaces, () -> new PollException(ErrorCode.NO_POLL_ITEMS));
        ExceptionUtils.checkParamsNotNull(timeConstraint);

        this.menuDate = menuDate == null ? LocalDate.now() : menuDate;
        this.checkMenuDate(lunchPlaces, menuDate);

        this.timeConstraint = timeConstraint;
        this.pollItems = this.convertToPollItems(lunchPlaces);
        this.votes = new HashSet<>();
        this.policies = this.registerPolicies();
    }

    @Builder(toBuilder = true)
    private LunchPlacePoll(String id, Integer version, TimeConstraint timeConstraint,
                           List<PollItem> pollItems, LocalDate menuDate, Set<Vote> votes) {
        super(id, version);
        this.timeConstraint = timeConstraint;
        this.pollItems = pollItems;
        this.menuDate = menuDate;
        this.votes = votes;
        this.policies = this.registerPolicies();
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

    private LunchPlacePoll addVote(Vote vote) {
        this.votes.add(vote);
        return this;
    }

    private LunchPlacePoll addVotes(Set<Vote> votes) {
        this.votes.addAll(votes);
        return this;
    }

    private void replaceVotes(Collection<Vote> oldVotes, Vote newVote) {
        oldVotes.forEach(this.votes::remove);
        this.votes.add(newVote);
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

    private PollItem pollItemById(String id) {
        Objects.requireNonNull(id, "PollItemId must not be null");
        return pollItems.stream()
                .filter(pollItem -> id.equals(pollItem.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Poll %s does not contain item %s", this.getId(), id)));
    }

    public List<PollItem> getPollItems() {
        return Collections.unmodifiableList(this.pollItems);
    }

    public Set<Vote> getVotes() {
        return Collections.unmodifiableSet(this.votes);
    }

    public String toString() {
        return "LunchPlacePoll{" +
                "id=" + id +
                "timeConstraint=" + timeConstraint +
                ", menuDate=" + menuDate +
                '}';
    }

    @Override
    public int compareTo(LunchPlacePoll o) {
        int r = this.getMenuDate().compareTo(o.getMenuDate());
        return r != 0 ? (-1)*r : this.getId().compareTo(o.getId());
    }
}