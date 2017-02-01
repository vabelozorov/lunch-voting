package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import ua.belozorov.lunchvoting.exceptions.*;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.AcceptPolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.CommonPolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.VoteForAnotherUpdatePolicy;
import ua.belozorov.lunchvoting.model.voting.polling.votepolicies.VotePolicy;

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
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
@Entity
@Table(name = "polls")
@Getter(AccessLevel.PACKAGE)
public final class LunchPlacePoll extends AbstractPersistableObject implements Poll {
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(12, 0);
    private static final LocalTime DEFAULT_ALLOW_CHANGE_VOTE_TIME = LocalTime.of(11, 0);

    private final TimeConstraint timeConstraint;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "poll", cascade = CascadeType.PERSIST)
//    @OrderBy("position ASC")
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
        this.timeConstraint = new TimeConstraint(startTime, endTime,voteChangeTimeThreshold);
        this.pollItems = this.convertToPollItems(lunchPlaces);
        this.menuDate = menuDate;
        this.votes = new HashSet<>();
        this.policies = this.registerPolicies();
    }

    @Builder(toBuilder = true)
    LunchPlacePoll(String id, Integer version, TimeConstraint timeConstraint,
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
        if (lunchPlaces == null) {
            lunchPlaces = Collections.emptyList();
        }
        List<PollItem> pollItems = new ArrayList<>();
        for (int i = 0; i < lunchPlaces.size(); i++) {
            pollItems.add(new PollItem(lunchPlaces.get(i), this));
        }
        return Collections.unmodifiableList(pollItems);
    }

    private void checkLunchPlaceDate(List<LunchPlace> places, LocalDate date) {
        for (LunchPlace place : places) {
            if (place.getMenus().isEmpty()) {
                throw new LunchPlaceWithoutMenuException();
            } else {
                for (Menu m : place.getMenus()) {
                    if ( ! m.getEffectiveDate().equals(date)) {
                        throw new MenuDateMismatchException();
                    }
                }
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

    @Override
    public VotePolicyDecision registerVote(String voterId, String pollItemId) {
        Set<Vote> votersVotes = this.votes.stream()
                                            .filter(v -> v.getVoterId().equals(voterId))
                                            .collect(Collectors.toSet());
        VoteIntention intention = new VoteIntentionImpl(voterId, this.pollItemById(pollItemId), votersVotes);

        return this.policies.stream()
                .map(policy -> policy.checkCompliance(intention))
                .filter(vd -> ! vd.isContinue())
                .findFirst()
                .orElseThrow(() -> new NoVotePolicyMatchException(intention));
    }

    PollItem pollItemById(String id) {
        Objects.requireNonNull(id, "PollItemId must not be null");
        return pollItems.stream()
                .filter(pollItem -> id.equals(pollItem.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Poll %s does not contain item %s", this.getId(), id)));
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

    @Override
    public List<PollItem> getPollItems() {
        return this.pollItems;
    }

    @Override
    public LocalDate getMenuDate() {
        return this.menuDate;
    }

    @Override
    public Set<Vote> getVotes() {
        return Collections.unmodifiableSet(this.votes);
    }

//    public LunchPlacePollBuilder toBuilder() {
//        return new LunchPlacePollBuilder(this);
//    }
//
//    public static class LunchPlacePollBuilder {
//        private String id;
//        private Integer version;
//        private TimeConstraint timeConstraint;
//        private Set<PollItem> pollItems;
//        private Set<Vote> votes;
//        private LocalDate menuDate;
//
//        private LunchPlacePollBuilder(LunchPlacePoll poll) {
//            this.id = poll.id;
//            this.version = poll.version;
//            this.timeConstraint = poll.timeConstraint;
//            this.pollItems = poll.pollItems;
//            this.votes = poll.votes;
//            this.menuDate = poll.menuDate;
//        }
//
//        LunchPlacePollBuilder timeConstraint(TimeConstraint timeConstraint) {
//            this.timeConstraint = timeConstraint;
//            return this;
//        }
//
//        LunchPlacePollBuilder pollItems(Set<PollItem> pollItems) {
//            this.pollItems = pollItems;
//            return this;
//        }
//
//        public LunchPlacePollBuilder votes(Set<Vote> votes) {
//            this.votes = votes;
//            return this;
//        }
//
//        public LunchPlacePollBuilder menuDate(LocalDate date) {
//            this.menuDate = date;
//            return this;
//        }
//
//        public LunchPlacePoll build() {
//            return new LunchPlacePoll(id, version, timeConstraint, pollItems, menuDate, votes);
//        }
//    }
}