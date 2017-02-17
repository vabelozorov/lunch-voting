package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;

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
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null").withNano(0);
        this.endTime = Objects.requireNonNull(endTime, "endTime must not be null").withNano(0);
        this.voteChangeThreshold = Objects.requireNonNull(voteChangeThreshold, "voteChangeThreshold must not be null").withNano(0);

        if ( ! this.startTime.isBefore(this.endTime)) {
            throw new IllegalStateException("startTime must be before endTime");
        }

        if ( ! this.voteChangeThreshold.isBefore(this.endTime) || this.voteChangeThreshold.isBefore(this.startTime)) {
            throw new IllegalStateException("invariant [startTime <= voteChangeThreshold < endTime] violated");
        }
    }

    public boolean isInTimeToChangeVote(LocalDateTime dateTime) {
        return ! (dateTime.isBefore(startTime) || dateTime.isAfter(voteChangeThreshold));
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    public boolean isPollActive(LocalDateTime dateTime) {
        return ! dateTime.isBefore(startTime) && dateTime.isBefore(endTime);
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
