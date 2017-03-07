package ua.belozorov.lunchvoting.model.voting.polling;

import lombok.AccessLevel;
import lombok.Getter;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.PollException;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 *
 * Created on 09.12.16.
 */
@Embeddable
@Getter(AccessLevel.PUBLIC)
public final class TimeConstraint {
    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(12, 0);
    private static final LocalTime DEFAULT_ALLOW_CHANGE_VOTE_TIME = LocalTime.of(11, 0);

    public static TimeConstraint getDefault() {
        return new TimeConstraint();
    }

    @Column(name = "start_time")
    private final LocalDateTime startTime;

    @Column(name = "end_time")
    private final LocalDateTime endTime;

    @Column(name = "change_time")
    private final LocalDateTime voteChangeThreshold;

    protected TimeConstraint() {
        this(null, null, null);
    }

    /**
     *
     * @param startTime time from which the poll starts to accept votes.
     * @param endTime starting from this time votes will be rejected.
     * @param voteChangeThreshold time until which (included) a voter is allowed to change his/her mind.
     */
    public TimeConstraint(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime voteChangeThreshold) {
        LocalDate now = LocalDate.now();
        startTime = startTime == null ? LocalDateTime.of(now, DEFAULT_START_TIME) : startTime;
        endTime = endTime == null ? LocalDateTime.of(now, DEFAULT_END_TIME) : endTime;
        voteChangeThreshold = voteChangeThreshold == null ?
                LocalDateTime.of(now, DEFAULT_ALLOW_CHANGE_VOTE_TIME) : voteChangeThreshold;

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
