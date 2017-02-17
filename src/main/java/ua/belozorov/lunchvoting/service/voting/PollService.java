package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.02.17.
 */
public interface PollService {

    /**
     * Creates a Poll instance where PollItems are a set of LunchPlace instances and each such instance has a Menu
     * for today
     * @return ID of the created Poll instance
     * @param areaId
     */
    LunchPlacePoll createPollForTodayMenus(String areaId);

    LunchPlacePoll createPollForMenuDate(String areaId, LocalDate date);

    LunchPlacePoll getWithPollItems(String areaId, String pollId);

    LunchPlacePoll getWithPollItemsAndVotes(String areaId, String pollId);

    List<LunchPlacePoll> getAll(String areaId);

    List<LunchPlacePoll> getPollsByActivePeriod(String areaId, LocalDateTime startDt, LocalDateTime endDt);

    List<LunchPlacePoll> getPastPolls(String areaId);

    List<LunchPlacePoll> getActivePolls(String areaId);

    List<LunchPlacePoll> getFuturePolls(String areaId);

    Boolean isPollActive(String areaId, String pollId);

    void delete(String areaId, String pollId);
}
