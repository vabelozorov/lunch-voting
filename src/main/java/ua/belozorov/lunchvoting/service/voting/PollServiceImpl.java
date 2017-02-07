package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 01.02.17.
 */
@Transactional(readOnly = true)
@Service
public final class PollServiceImpl implements PollService {

    private final LunchPlaceRepository lunchPlaceRepository;

    private final PollRepository pollRepository;

    @Autowired
    public PollServiceImpl(LunchPlaceRepository lunchPlaceRepository, PollRepository pollRepository) {
        this.lunchPlaceRepository = lunchPlaceRepository;
        this.pollRepository = pollRepository;
    }

    /**
     * Create a poll where poll items are composed of all currently available menus for today date.
     * @return Poll
     */
    @Override
    @Transactional
    //TODO scheduled job?
    public Poll createPollForTodayMenus() {
        return this.createPollForMenuDate(LocalDate.now());
    }

    @Override
    @Transactional
    public Poll createPollForMenuDate(LocalDate menuDate) {
        List<LunchPlace> places = lunchPlaceRepository.getIfMenuForDate(menuDate);
        LunchPlacePoll poll = new LunchPlacePoll(places, menuDate);
        pollRepository.savePoll(poll);
        return poll;
    }

    @Override
    @Transactional
    public void delete(String id) {
        if ( ! pollRepository.removePoll(id)) {
            throw new NotFoundException(id, LunchPlacePoll.class);
        }
    }

    @Override
    public List<Poll> getAll() {
        return this.castToPoll(pollRepository.getAllPolls());
    }

    @Override
    public Poll get(String id) {
        return ofNullable(pollRepository.get(id))
                .orElseThrow(() -> new NotFoundException(id, LunchPlacePoll.class));
    }

    @Override
    public List<Poll> getPollsByActivePeriod(LocalDateTime startDt, LocalDateTime endDt) {
        return this.castToPoll(
                pollRepository.getPollByActivePeriod(startDt, endDt)
        );
    }

    @Override
    public List<Poll> getActivePolls() {
        LocalDateTime now = LocalDateTime.now();
        return this.getPollsByActivePeriod(now, now);
    }

    @Override
    public List<Poll> getFuturePolls() {
        return this.castToPoll(pollRepository.getFuturePolls());
    }

    @Override
    public List<Poll> getPastPolls() {
        return this.castToPoll(pollRepository.getPastPolls());
    }

    @Override
    public Boolean isPollActive(String id) {
        return pollRepository.isActive(id);
    }

    private List<Poll> castToPoll(Collection<LunchPlacePoll> list) {
        return list.stream().map(poll -> (Poll)poll).collect(Collectors.toList());
    }
}
