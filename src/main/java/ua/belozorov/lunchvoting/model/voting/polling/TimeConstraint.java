package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;
import ua.belozorov.lunchvoting.exceptions.PollException;
import ua.belozorov.lunchvoting.util.ExceptionUtils;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
@Embeddable
@Getter(AccessLevel.PUBLIC)
public final class TimeConstraint {
    @Column(name = "start_time")
    private final LocalDateTime startTime;

    @Column(name = "end_time")
    private final LocalDateTime endTime;

    @Column(name = "change_time")
    private final LocalDateTime voteChangeThreshold;

    protected TimeConstraint() {
        startTime = null;
        endTime = null;
        voteChangeThreshold = null;
    }

    TimeConstraint(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime voteChangeThreshold) {
        ExceptionUtils.checkParamsNotNull(startTime, endTime, voteChangeThreshold);

        this.startTime = startTime.withNano(0);
        this.endTime = endTime.withNano(0);
        this.voteChangeThreshold = voteChangeThreshold.withNano(0);

        if ( this.endTime.isBefore(this.startTime)) {
            throw new PollException(ErrorCode.TIMECONSTRAINT_END_BEFORE_START);
        }

        if ( this.voteChangeThreshold.isAfter(this.endTime) || this.voteChangeThreshold.isBefore(this.startTime)) {
            throw new PollException(ErrorCode.VOTECHANGETHRESHOLD_INVALID);
        }
    }

    public boolean isInTimeToChangeVote(LocalDateTime dateTime) {
        return this.isPollActive(dateTime) && !dateTime.isAfter(voteChangeThreshold);
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isPollActive(LocalDateTime dateTime) {
        return ! (dateTime.isBefore(startTime) ||  dateTime.isAfter(endTime));
    }

    @Override
    public String toString() {
        return "TimeConstraint{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", voteChangeThreshold=" + voteChangeThreshold +
                '}';
    }
}
