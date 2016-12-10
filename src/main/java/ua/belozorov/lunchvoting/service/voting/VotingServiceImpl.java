package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollingRepository;

import java.time.LocalDate;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
@Service
@Transactional(readOnly = true)
public class VotingServiceImpl implements VotingService {

    @Autowired
    private LunchPlaceRepository lunchPlaceRepository;

    @Autowired
    private PollingRepository pollingRepository;

    @Override
    @Transactional
    public void setPollingDefaultInterval(PollingTimeInterval interval) {
        pollingRepository.setPollingInternal(interval);
    }

    @Override
    public PollingTimeInterval getDefaultPollInterval() {
        return pollingRepository.getPollingInterval();
    }

    /**
     * Create a poll where poll items are composed of all currently available menus for today date.
     * @return Poll
     */
    @Override
    public Poll createPollForTodayMenus() {
        List<LunchPlace> todayMenus = lunchPlaceRepository.getByMenusForDate(LocalDate.now());
        Poll poll = new Poll(todayMenus);
        pollingRepository.savePoll(poll);
        //TODO scheduled job?
        return pollingRepository.getPollAndEmptyPollItems(poll.getId());
    }

    @Override
    public void vote(String voterId, String pollId, String pollItemId) {
        Poll poll = ofNullable(pollingRepository.getPollAndEmptyPollItems(pollId))
                                .orElseThrow(() -> new NotFoundException(pollId, Poll.class));
        final Vote existingVote = pollingRepository.getVoteInPoll(voterId, pollId);
        VoteDecision decision = poll.verify(new VoteIntention(voterId, pollId, pollItemId, existingVote));

        if (decision.isAccept()) {
            pollingRepository.saveVote(decision.getVote());
        } else if (decision.isUpdate()) {
            pollingRepository.removeVote(existingVote);
            pollingRepository.saveVote(decision.getVote());
        } else {
            throw new IllegalStateException("Unexpected VoteDecision state");
        }
    }

    @Override
    public PollItem getPollItemDetails(String pollId, String pollItemId) {
        return pollingRepository.getPollItemWithPoll(pollId, pollItemId);
    }
}
