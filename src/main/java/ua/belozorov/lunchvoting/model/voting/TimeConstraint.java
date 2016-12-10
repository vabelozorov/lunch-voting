package ua.belozorov.lunchvoting.model.voting;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 09.12.16.
 */
@Embeddable
final class TimeConstraint {
    private final LocalDateTime startTime;

    private final LocalDateTime endTime;

    private final LocalDateTime voteChangeThreshold;

    protected TimeConstraint() {
        startTime = null;
        endTime = null;
        voteChangeThreshold = null;
    }

    TimeConstraint(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime voteChangeThreshold) {
        this.startTime = Objects.requireNonNull(startTime, "startTime must not be null");
        this.endTime = Objects.requireNonNull(endTime, "endTime must not be null");
        this.voteChangeThreshold = Objects.requireNonNull(voteChangeThreshold, "voteChangeThreshold must not be null");

        if ( ! this.startTime.isBefore(this.endTime)) {
            throw new IllegalStateException("startTime must be before endTime");
        }

        if ( ! this.voteChangeThreshold.isBefore(this.endTime) || this.voteChangeThreshold.isBefore(this.startTime)) {
            throw new IllegalStateException("invariant [startTime <= voteChangeThreshold < endTime] violated");
        }
    }

    boolean isInTimeToChangeVote(LocalDateTime dateTime) {
        return ! (dateTime.isBefore(startTime) || dateTime.isAfter(voteChangeThreshold));
    }

    @SuppressWarnings("JpaAttributeMemberSignatureInspection")
    boolean isPollActive(LocalDateTime dateTime) {
        return ! dateTime.isBefore(startTime) && dateTime.isBefore(endTime);
    }
}
