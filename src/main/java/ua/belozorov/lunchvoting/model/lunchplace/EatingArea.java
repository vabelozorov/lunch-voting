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
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
@Entity
@Table(name = "areas")
@Getter(AccessLevel.PUBLIC)
public final class EatingArea extends AbstractPersistableObject {

    @NotBlank
    @SafeHtml
    private final String name;

    @OneToMany
    @OrderBy("email")
    @JoinColumn(name = "area_id")
    @Fetch(FetchMode.SUBSELECT)
    private final Set<User> users;

    @OneToMany
    @OrderBy("name")
    @JoinColumn(name = "area_id")
    @Fetch(FetchMode.SUBSELECT)
    private final Set<LunchPlace> places;

    @OneToMany
    @OrderBy("menuDate")
    @JoinColumn(name = "area_id")
    @Fetch(FetchMode.SUBSELECT)
    private final Set<LunchPlacePoll> polls;

    private final LocalDateTime created;

    protected EatingArea() {
        this.name = null;
        this.users = null;
        this.places = null;
        this.polls = null;
        this.created = null;
    }

    public EatingArea(String name, User creator) {
        this(null, name, creator);
    }

    public EatingArea(String id, String name, User creator) {
        super(id, null);
        this.name = name;
        this.users = new HashSet<>(Collections.singleton(creator));
        this.places = new HashSet<>();
        this.polls = new HashSet<>();
        this.created = LocalDateTime.now().withNano(0);
    }

    @Builder(toBuilder = true)
    private EatingArea(String id, Integer version, String name, @Singular  Set<User> users,
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

    public EatingArea join(JoinAreaRequest request) {
        return this.toBuilder().user(request.getRequester()).build();
    }

    public Set<User> getUsers() {
        return ImmutableSortedSet.copyOf(Comparator.comparing(User::getEmail), this.users);
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
