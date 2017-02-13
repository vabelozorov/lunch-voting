package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
@Service
@Transactional(readOnly = true)
public final class VotingServiceImpl implements VotingService {

    @Autowired
    private LunchPlaceRepository lunchPlaceRepository;

    @Autowired
    private PollRepository pollRepository;

    @Override
    @Transactional
    public Vote vote(String voterId, String pollId, String pollItemId) {
        ExceptionUtils.checkParamsNotNull(voterId, pollId, pollItemId);
        Poll poll = ofNullable(pollRepository.getWithVotes(pollId))
                            .orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));

        VotePolicyDecision decision = poll.registerVote(voterId, pollItemId);
        Vote acceptedVote = decision.getAcceptedVote();
        if (decision.isAccept()) {
            pollRepository.saveVote(acceptedVote);
        } else if (decision.isUpdate()) {
            pollRepository.replaceVote(decision.votesToBeRemoved(), acceptedVote);
        } else {
            throw new IllegalStateException("Unknown vote decision");
        }
        return acceptedVote;
    }


    @Override
    public VotingResult<PollItem> getPollResult(String pollId) {
        ExceptionUtils.checkParamsNotNull(pollId);

        LunchPlacePoll poll = ofNullable(pollRepository.getPollAndPollItemsAndVotes(pollId)).
                orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));
        PollVoteResult<PollItem> pollResult = new PollVoteResult<>(poll, Vote::getPollItem);
        return pollResult;
    }

    @Override
    public Collection<String> getVotedByVoter(String pollId, String voterId) {
        ExceptionUtils.checkParamsNotNull(pollId, voterId);

        return pollRepository.getVotedByVoter(pollId, voterId);
    }

    @Override
    public Collection<Vote> getVotesForPoll(String pollId) {
        return pollRepository.getVotesForPoll(pollId);
    }
}
