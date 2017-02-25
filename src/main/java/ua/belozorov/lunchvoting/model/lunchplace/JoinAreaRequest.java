package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Builder;
import lombok.Getter;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <h2></h2>
 *
 * Created on 09.02.17.
 */
@Getter
@Entity
@Table(name = "join_requests")
public final class JoinAreaRequest extends AbstractPersistableObject {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private final User requester;

    @Column(name = "created")
    private final LocalDateTime created;

    @Column(name = "decided_on")
    private final LocalDateTime decidedOn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private final EatingArea area;

    @Column(name = "status")
    private JoinStatus status;

    protected JoinAreaRequest() {
        this.requester = null;
        this.created = null;
        this.area = null;
        this.status = null;
        this.decidedOn = null;
    }

    public JoinAreaRequest(User requester, EatingArea area) {
        this(null, null, requester, area, LocalDateTime.now(), null, JoinStatus.PENDING);
    }

    @Builder(toBuilder = true)
    private JoinAreaRequest(String id, Integer version, User requester, EatingArea area, LocalDateTime created, LocalDateTime decidedOn, JoinStatus status) {
        super(id, version);
        this.requester = requester;
        this.area = area;
        this.created = created;
        this.decidedOn = decidedOn;
        this.status = status;
    }

    public JoinAreaRequest approve() {
        return this.changeStatus(JoinStatus.APPROVED);
    }

    public JoinAreaRequest reject() {
        return this.changeStatus(JoinStatus.REJECTED);
    }

    public JoinAreaRequest cancel() {
        return this.changeStatus(JoinStatus.CANCELLED);
    }

    public boolean isRequestMaker(User user) {
        return this.requester.equals(user);
    }

    private JoinAreaRequest changeStatus(JoinStatus status) {
        return this.toBuilder().status(status).decidedOn(LocalDateTime.now()).build();
    }

    @Override
    public String toString() {
        return "JoinAreaRequest{" +
                "requesterName=" + requester.getName() +
                ", created=" + created +
                ", decidedOn=" + decidedOn +
                ", areaName=" + area.getName() +
                ", status=" + status +
                '}';
    }

    public enum JoinStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }
}
