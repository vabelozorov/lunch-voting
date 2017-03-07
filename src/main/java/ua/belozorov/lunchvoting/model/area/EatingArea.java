package ua.belozorov.lunchvoting.model.area;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;
import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 * <p>Immutable class which groups together voters, lunch places they vote for, and polls for the best lunch place</p>
 * Created on 08.02.17.
 */
@Entity
@Table(name = "areas")
@Getter(AccessLevel.PUBLIC)
public final class EatingArea extends AbstractPersistableObject {

    private final String name;

    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("email")
    @JoinColumn(name = "area_id")
    private final Set<User> voters;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("name")
    @JoinColumn(name = "area_id")
    private final Set<LunchPlace> places;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("menuDate")
    @JoinColumn(name = "area_id")
    private final Set<LunchPlacePoll> polls;

    private final LocalDateTime created;

    /**
     * JPA
     */
    EatingArea() {
        this.name = null;
        this.voters = new HashSet<>();
        this.places = new HashSet<>();
        this.polls = new HashSet<>();
        this.created = null;
    }

    /**
     * Constructor with auto-generating ID
     * @param name name of area, must be unique
     */
    public EatingArea(String name) {
        this(null, name);
    }

    /**
     * @param id any string or null to auto-generate
     * @param name name of area, must be unique
     */
    public EatingArea(@Nullable String id, String name) {
        //TODO using Collections.emptySet() causes failure during merge (for voters). Read merge definition
        this(id, null, name, new HashSet<>(),new HashSet<>(), new HashSet<>(), LocalDateTime.now().withNano(0));
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param name name of area, must be unique
     * @param users
     * @param places
     * @param polls
     * @param created
     */
    private EatingArea(@Nullable String id, @Nullable Integer version, String name, Set<User> users,
                       Set<LunchPlace> places, Set<LunchPlacePoll> polls, LocalDateTime created) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, name, users, places, polls, created);

        this.name = name;
        this.voters = users;
        this.places = places;
        this.polls = polls;
        this.created = created;
    }

    public EatingArea withName(String name) {
        return new EatingArea(id, version, name, voters, places, polls, created);
    }

    /**
     * @param member user to add
     * @return a new instance of the class with the user added. The user must have its <code>areaId</code> set the ID of this instance
     */
    public EatingArea addMember(User member) {
        ofNullable(member.getAreaId())
                .filter(this.id::equals)
                .orElseThrow(() -> new IllegalStateException("Area member ID is null or belongs to other area"));
        Set<User> voters = new HashSet<>(this.voters);
        voters.add(member);
        return new EatingArea(id, version, name, voters, places, polls, created);
    }

    public EatingArea addPlace(LunchPlace place) {
        Set<LunchPlace> places = Sets.newHashSet(this.places);
        places.add(place);
        return new EatingArea(id, version, name, voters, places, polls, created);
    }

    public EatingArea withVoters(Set<User> voters) {
        return new EatingArea(id, version, name, Sets.newHashSet(voters), places, polls, created);
    }

    public EatingArea withPlaces(Set<LunchPlace> places) {
        return new EatingArea(id, version, name, voters, Sets.newHashSet(places), polls, created);
    }

    public EatingArea withPolls(Set<LunchPlacePoll> polls) {
        return new EatingArea(id, version, name, voters, places, Sets.newHashSet(polls), created);
    }

    public EatingArea addPoll(LunchPlacePoll poll) {
        Set<LunchPlacePoll> polls = Sets.newHashSet(this.polls);
        polls.add(poll);
        return new EatingArea(id, version, name, voters, places, polls, created);
    }

    public Set<User> getVoters() {
        return ImmutableSortedSet.copyOf(this.voters);
    }

    public Set<LunchPlacePoll> getPolls() {
        return ImmutableSortedSet.copyOf(this.polls);
    }

    public Set<LunchPlace> getPlaces() {
        return ImmutableSortedSet.copyOf(this.places);
    }

    @Override
    public String toString() {
        return "EatingArea{" +
                "id='" + id + '\'' +
                "version='" + version + '\'' +
                "name='" + name + '\'' +
                ", created=" + created +
                '}';
    }
}
