package ua.belozorov.lunchvoting.model.voting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Objects;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@Getter
public class PollingTimeInterval {
    @NotNull
    private final LocalTime pollingStartTime;
    @NotNull
    private final LocalTime pollingEndTime;

    public PollingTimeInterval(LocalTime pollingStartTime, LocalTime pollingEndTime) {
        this.pollingStartTime = Objects.requireNonNull(pollingStartTime);
        this.pollingEndTime = Objects.requireNonNull(pollingEndTime);
    }
}
