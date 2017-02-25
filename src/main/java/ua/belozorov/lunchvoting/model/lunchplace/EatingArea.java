package ua.belozorov.lunchvoting.model.lunchplace;

import com.google.common.collect.ImmutableSortedSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import org.jetbrains.annotations.Nullable;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * Created on 08.02.17.
 */
@Entity
@Table(name = "areas")
@Getter(AccessLevel.PUBLIC)
public final class EatingArea extends AbstractPersistableObject {

    @NotBlank
    @SafeHtml
    private final String name;

    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("email")
    @JoinColumn(name = "area_id")
    private final Set<User> users;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("name")
    @JoinColumn(name = "area_id")
    private final Set<LunchPlace> places;

    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("menuDate")
    @JoinColumn(name = "area_id")
    private final Set<LunchPlacePoll> polls;

    private final LocalDateTime created;

    protected EatingArea() {
        this.name = null;
        this.users = null;
        this.places = null;
        this.polls = null;
        this.created = null;
    }

    public EatingArea(String name) {
        this(null, name);
    }

    public EatingArea(@Nullable String id, String name) {
        this(id, null, name, new HashSet<>(), new HashSet<>(), new HashSet<>(), LocalDateTime.now().withNano(0));
    }

    @Builder(toBuilder = true)
    private EatingArea(@Nullable String id, @Nullable Integer version, String name, Set<User> users,
                       Set<LunchPlace> places, Set<LunchPlacePoll> polls, LocalDateTime created) {
        super(id, version);
        this.name = name;
        this.users = users;
        this.places = places;
        this.polls = polls;
        this.created = created;
    }

    public EatingArea changeName(String name) {
        return this.toBuilder().name(name).build();
    }

    public EatingArea addMember(User member) {
        ofNullable(member.getAreaId())
                .filter(this.id::equals)
                .orElseThrow(() -> new IllegalStateException("Area member ID is null or belongs to other area"));
        this.users.add(member);
        return this.toBuilder().build();
    }

    public EatingArea addPlace(LunchPlace place) {
        this.places.add(place);
        return this.toBuilder().build();
    }

    public EatingArea addPoll(LunchPlacePoll poll) {
        this.polls.add(poll);
        return this.toBuilder().build();
    }

    public Set<User> getUsers() {
        return ImmutableSortedSet.copyOf(this.users);
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
