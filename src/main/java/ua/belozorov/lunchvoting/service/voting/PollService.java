package ua.belozorov.lunchvoting.service.voting;

import org.jetbrains.annotations.Nullable;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.web.security.IsAdmin;
import ua.belozorov.lunchvoting.web.security.IsAdminOrVoter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * Created on 01.02.17.
 */
public interface PollService {

    LunchPlacePoll createPollForMenuDate(String areaId, LocalDate menuDate, TimeConstraint timeConstraint);

    LunchPlacePoll getWithPollItems(String areaId, String pollId);

    LunchPlacePoll getWithPollItemsAndVotes(String areaId, String pollId);

    List<LunchPlacePoll> getAll(String areaId);

    List<LunchPlacePoll> getPollsByActivePeriod(String areaId, @Nullable LocalDateTime startDt, @Nullable LocalDateTime endDt);

    List<LunchPlacePoll> getPastPolls(String areaId);

    List<LunchPlacePoll> getActivePolls(String areaId);

    List<LunchPlacePoll> getFuturePolls(String areaId);

    Boolean isPollActive(String areaId, String pollId);

    void delete(String areaId, String pollId);

    PollRepository getRepository();
}
