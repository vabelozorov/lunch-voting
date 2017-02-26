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

 *
 * Created on 29.11.16.
 */
@Service
@Transactional(readOnly = true)
public final class VotingServiceImpl implements VotingService {

    private final PollService pollService;
    private final PollRepository pollRepository;

    @Autowired
    public VotingServiceImpl(PollService pollService, PollRepository pollRepository) {
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
    public Vote getVote(String voterId, String voteId) {
        ExceptionUtils.checkParamsNotNull(voterId, voteId);

        return ofNullable(pollRepository.getVote(voterId, voteId))
                .orElseThrow(() -> new NotFoundException(voteId, Vote.class));
    }

    @Override
    public Vote getFullVote(String voterId, String voteId) {
        ExceptionUtils.checkParamsNotNull(voterId, voteId);

        return ofNullable(pollRepository.getFullVote(voterId, voteId))
                .orElseThrow(() -> new NotFoundException(voteId, Vote.class));
    }

    @Override
    public List<Vote> getFullVotesForPoll(String areaId, String pollId) {
        ExceptionUtils.checkParamsNotNull(areaId, pollId);

        return pollRepository.getFullVotesForPoll(areaId, pollId);
    }

    @Override
    public List<String> getVotedForPollByVoter(User voter, String pollId) {
        ExceptionUtils.checkParamsNotNull(pollId, voter);

        return pollRepository.getVotedByVoter(voter.getAreaId(), pollId, voter.getId());
    }

    @Override
    @Transactional
    public void revokeVote(String voterId, String voteId) {
        Vote vote = this.getVote(voterId, voteId);
        pollRepository.remove(vote);
    }

    @Override
    public VotingResult<PollItem> getPollResult(String areaId, String pollId) {
        ExceptionUtils.checkParamsNotNull(pollId);

        LunchPlacePoll poll = pollService.getWithPollItemsAndVotes(areaId, pollId);
        PollVoteResult<PollItem> pollResult = new PollVoteResult<>(poll, Vote::getPollItem);
        return pollResult;
    }

    private void replaceVote(Set<Vote> forRemoval, Vote acceptedVote) {
        ExceptionUtils.checkParamsNotNull(forRemoval, acceptedVote);

        pollRepository.remove(forRemoval);
        pollRepository.save(acceptedVote);
    }
}
