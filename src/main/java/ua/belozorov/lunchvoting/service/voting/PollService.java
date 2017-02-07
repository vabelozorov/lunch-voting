package ua.belozorov.lunchvoting.service.voting;

import ua.belozorov.lunchvoting.model.voting.polling.Poll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     */
    Poll createPollForTodayMenus();

    Poll createPollForMenuDate(LocalDate date);

    void delete(String id);

    List<Poll> getAll();

    Poll get(String id);

    List<Poll> getPollsByActivePeriod(LocalDateTime startDt, LocalDateTime endDt);

    List<Poll> getActivePolls();

    List<Poll> getFuturePolls();

    List<Poll> getPastPolls();

    Boolean isPollActive(String id);
}
