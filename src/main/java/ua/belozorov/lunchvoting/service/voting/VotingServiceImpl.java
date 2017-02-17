package ua.belozorov.lunchvoting.service.voting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.voting.*;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.model.voting.polling.votedecisions.VotePolicyDecision;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.voting.PollRepository;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.11.16.
 */
@Service
@Transactional(readOnly = true)
public final class VotingServiceImpl implements VotingService {

    private final PollService pollService;
    private final LunchPlaceRepository lunchPlaceRepository;
    private final PollRepository pollRepository;

    @Autowired
    public VotingServiceImpl(LunchPlaceRepository lunchPlaceRepository, PollService pollService, PollRepository pollRepository) {
        this.lunchPlaceRepository = lunchPlaceRepository;
        this.pollService = pollService;
        this.pollRepository = pollRepository;
    }

    @Override
    @Transactional
    public Vote vote(User voter, String pollId, String pollItemId) {
        ExceptionUtils.checkParamsNotNull(voter, pollId, pollItemId);

        LunchPlacePoll poll = pollService.getWithPollItemsAndVotes(voter.getAreaId(), pollId);

        VotePolicyDecision decision = poll.registerVote(voter.getId(), pollItemId);
        Vote acceptedVote = decision.getAcceptedVote();
        if (decision.isAccept()) {
            pollRepository.save(acceptedVote);
        } else if (decision.isUpdate()) {
            this.replaceVote(decision.votesToBeRemoved(), acceptedVote);
        } else {
            throw new IllegalStateException("Unknown vote decision");
        }
        return acceptedVote;
    }


    @Override
    public VotingResult<PollItem> getPollResult(String areaId, String pollId) {
        ExceptionUtils.checkParamsNotNull(pollId);

        LunchPlacePoll poll = ofNullable(pollRepository.getWithPollItemsAndVotes(areaId, pollId)).
                orElseThrow(() -> new NotFoundException(pollId, LunchPlacePoll.class));
        PollVoteResult<PollItem> pollResult = new PollVoteResult<>(poll, Vote::getPollItem);
        return pollResult;
    }

    @Override
    public List<String> getVotedByVoter(User voter, String pollId) {
        ExceptionUtils.checkParamsNotNull(pollId, voter);

        return pollRepository.getVotedByVoter(voter.getAreaId(), pollId, voter.getId());
    }

    @Override
    public List<Vote> getVotesForPoll(String areaId, String pollId) {
        ExceptionUtils.checkParamsNotNull(areaId, pollId);

        return pollRepository.getVotesForPoll(areaId, pollId);
    }

    @Override
    public void replaceVote(Set<Vote> forRemoval, Vote acceptedVote) {
        ExceptionUtils.checkParamsNotNull(forRemoval, acceptedVote);

        pollRepository.remove(forRemoval);
        pollRepository.save(acceptedVote);
    }

//    @Override
//    public void revokeVote(String areaId, String voteId) {
//        pollRepository.
//    }
}
