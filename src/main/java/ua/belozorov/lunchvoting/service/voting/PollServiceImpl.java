package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.TimeConstraint;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 *
 * Created on 01.02.17.
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

    @Override
    @Transactional
    public LunchPlacePoll createPollForMenuDate(String areaId, LocalDate menuDate, TimeConstraint timeConstraint) {
        List<LunchPlace> places = lunchPlaceRepository.getIfMenuForDate(areaId, menuDate);
        LunchPlacePoll poll = new LunchPlacePoll(timeConstraint, places, menuDate);
        pollRepository.save(poll);
        return poll;
    }

    @Override
    public LunchPlacePoll getWithPollItems(String areaId, String pollId) {
        return ofNullable(pollRepository.getWithPollItems(areaId, pollId))
                .orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));
    }

    @Override
    public LunchPlacePoll getWithPollItemsAndVotes(String areaId, String pollId) {
        return ofNullable(pollRepository.getWithPollItemsAndVotes(areaId, pollId))
                .orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));
    }

    @Override
    public List<LunchPlacePoll> getAll(String areaId) {
        return pollRepository.getAll(areaId);
    }

    @Override
    public List<LunchPlacePoll> getPollsByActivePeriod(String areaId, LocalDateTime startDt, LocalDateTime endDt) {
        startDt = startDt == null ? LocalDateTime.of(1900, 1, 1, 0, 0) : startDt;
        endDt = endDt == null ? LocalDateTime.of(7777, 1, 1, 0, 0) : endDt;
        return pollRepository.getPollByActivePeriod(areaId, startDt, endDt);
    }

    @Override
    public List<LunchPlacePoll> getPastPolls(String areaId) {
        return pollRepository.getPastPolls(areaId);
    }

    @Override
    public List<LunchPlacePoll> getActivePolls(String areaId) {
        LocalDateTime now = LocalDateTime.now();
        return this.getPollsByActivePeriod(areaId, now, now);
    }

    @Override
    public List<LunchPlacePoll> getFuturePolls(String areaId) {
        return pollRepository.getFuturePolls(areaId);
    }

    @Override
    public Boolean isPollActive(String areaId, String pollId) {
        return pollRepository.isActive(areaId, pollId);
    }

    @Override
    @Transactional
    public void delete(String areaId, String pollId) {
        if ( ! pollRepository.removePoll(areaId, pollId)) {
            throw new NotFoundException(pollId, LunchPlacePoll.class);
        }
    }

    @Override
    public PollRepository getRepository() {
        return this.pollRepository;
    }
}
