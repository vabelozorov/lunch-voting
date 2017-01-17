package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollingRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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


    /**
     * Create a poll where poll items are composed of all currently available menus for today date.
     * @return Poll
     */
    @Override
    @Transactional
    //TODO scheduled job?
    public String createPollForTodayMenus() {
        LocalDate menuDate = LocalDate.now();
        List<LunchPlace> places = lunchPlaceRepository.getIfMenuForDate(menuDate);
        LunchPlacePoll poll = new LunchPlacePoll(places, menuDate);
        pollingRepository.savePoll(poll);
        return poll.getId();
    }

    @Override
    @Transactional
    public Vote vote(String voterId, String pollId, String pollItemId) {
        ExceptionUtils.checkAllNotNull(voterId, pollId, pollItemId);

        Poll poll = ofNullable(pollingRepository.getPollWithVotesAndEmptyPollItems(pollId))
                .orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));
        final Vote existingVote = pollingRepository.getVoteInPoll(voterId, pollId);
        VoteDecision decision = poll.verify(new VoteIntention(voterId, pollItemId, existingVote));

        Vote vote = decision.getVote();
        if (decision.isAccept()) {
            pollingRepository.saveVote(vote);
        } else if (decision.isUpdate()) {
            pollingRepository.removeVote(existingVote);
            pollingRepository.saveVote(vote);
        } else {
            throw new IllegalStateException("Unexpected VoteDecision state");
        }
        return vote;
    }

    @Override
    public Poll getPollFullDetails(String pollId) {
        Objects.requireNonNull(pollId);
        return pollingRepository.getFullPoll(pollId);
    }

    @Override
    public Poll getPollItemDetails(String pollId, String pollItemId) {
        return this.getPollItemDetails(pollId, Collections.singletonList(pollItemId));
    }

    @Override
    public Poll getPollItemDetails(String pollId, Collection<String> pollItemIds) {
        ExceptionUtils.requireNonNullNotEmpty(pollItemIds);
        Objects.requireNonNull(pollId);
        return pollingRepository.getPollAndPollItem(pollId, pollItemIds);
    }

    @Override
    public VoteStatistics<PollItem> getPollResultPerItem(String pollId) {
        return null;
    }
}
