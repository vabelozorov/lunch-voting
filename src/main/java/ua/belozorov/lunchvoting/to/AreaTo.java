package ua.belozorov.lunchvoting.to;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 12.02.17.
 */
@Getter
public final class AreaTo {
    private final String id;
    private final String name;
    private final LocalDateTime created;
    private final List<String> users;
    private final List<String> polls;
    private final List<String> places;
    private final Long userCount;
    private final Long placeCount;
    private final Long pollCount;

    public AreaTo(String id, String name, LocalDateTime created, List<String> userIds, List<String> pollIds, List<String> placeIds) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.users = userIds;
        this.polls = pollIds;
        this.places = placeIds;
        this.userCount = null;
        this.placeCount = null;
        this.pollCount = null;
    }

    public AreaTo(String id, String name, LocalDateTime created, Long userCount, Long placeCount, Long pollCount) {
        this.id = id;
        this.name = name;
        this.created = created;
        this.userCount = userCount;
        this.placeCount = placeCount;
        this.pollCount = pollCount;
        this.users = null;
        this.polls = null;
        this.places = null;
    }

    @Override
    public String toString() {
        return "AreaTo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", users=" + users +
                ", polls=" + polls +
                ", places=" + places +
                ", userCount=" + userCount +
                ", placeCount=" + placeCount +
                ", pollCount=" + pollCount +
                '}';
    }
}
