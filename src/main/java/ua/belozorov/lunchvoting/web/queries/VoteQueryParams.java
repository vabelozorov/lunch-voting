package ua.belozorov.lunchvoting.web.queries;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.annotation.PostConstruct;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 31.01.17.
 */
@Setter(AccessLevel.PUBLIC)
@Getter(AccessLevel.PUBLIC)
public final class VoteQueryParams {
    private String voterId;
    private String pollId;
    private String itemId;

    protected VoteQueryParams() {
    }

    @PostConstruct
    public void init() {
        ExceptionUtils.checkAllNotNull(this.voterId, this.pollId, this.itemId);
    }
}
