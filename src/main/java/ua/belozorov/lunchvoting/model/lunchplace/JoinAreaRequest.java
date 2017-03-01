package ua.belozorov.lunchvoting.model.lunchplace;

import lombok.Builder;
import lombok.Getter;
import ua.belozorov.lunchvoting.exceptions.JoinRequestException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static ua.belozorov.lunchvoting.util.ExceptionUtils.NOT_CHECK;

/**
 *<p>Immutable class with stores data of a voter to join an area.</p>
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
    private final JoinStatus status;

    /**
     * JPA
     */
    JoinAreaRequest() {
        this.requester = null;
        this.created = null;
        this.area = null;
        this.status = null;
        this.decidedOn = null;
    }

    /**
     * Constructor with auto-generating ID
     * @param requester a voter who make a request
     * @param area an area that a requester want to join
     */
    public JoinAreaRequest(User requester, EatingArea area) {
        this(null, null, requester, area, LocalDateTime.now(), null, JoinStatus.PENDING);
    }

    /**
     * All-args constructor for cloning setters
     * @param id any string or null to auto-generate
     * @param version a positive value to indicate a persisted instance or null for a transient instance
     * @param requester a voter who make a request
     * @param area an area that a requester want to join
     * @param created
     * @param decidedOn
     * @param status
     */
    private JoinAreaRequest(String id, Integer version, User requester, EatingArea area, LocalDateTime created, LocalDateTime decidedOn, JoinStatus status) {
        super(id, version);

        ExceptionUtils.checkParamsNotNull(NOT_CHECK, NOT_CHECK, requester, area, created, NOT_CHECK, status);

        if (Objects.equals(area.getId(), requester.getAreaId())) {
            throw new JoinRequestException(ErrorCode.JAR_REQUESTER_ALREADY_IN_AREA);
        }

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
        return new JoinAreaRequest(id, version, requester, area, created, LocalDateTime.now(), status);
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
