package ua.belozorov.lunchvoting.to;

import lombok.Getter;
import ua.belozorov.lunchvoting.model.area.JoinAreaRequest;

import java.time.LocalDateTime;

/**

 *
 * Created on 12.02.17.
 */
@Getter
public final class JoinRequestTo {
    private final String id;
    private final String area;
    private final String requester;
    private final String status;
    private final LocalDateTime created;
    private final LocalDateTime decidedOn;

    public JoinRequestTo(JoinAreaRequest request) {
        this(request.getId(), request.getArea().getId(),
                request.getRequester().getId(), request.getStatus().name(), request.getCreated(), request.getDecidedOn());
    }

    public JoinRequestTo(String id, String areaId, String requesterId, String status, LocalDateTime created, LocalDateTime decidedOn) {
        this.id = id;
        this.area = areaId;
        this.requester = requesterId;
        this.status = status;
        this.created = created;
        this.decidedOn = decidedOn;
    }
}
