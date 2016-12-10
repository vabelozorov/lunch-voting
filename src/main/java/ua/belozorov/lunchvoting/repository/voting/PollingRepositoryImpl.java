package ua.belozorov.lunchvoting.repository.voting;

import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.model.voting.PollItem;
import ua.belozorov.lunchvoting.model.voting.PollingTimeInterval;
import ua.belozorov.lunchvoting.model.voting.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import javax.persistence.Query;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
@Repository
public class PollingRepositoryImpl extends BaseRepository implements PollingRepository {

    @Override
    public void setPollingInternal(PollingTimeInterval internal) {
        Query nativeQuery = em.createNativeQuery("INSERT INTO voting_config (poll_start_time, poll_end_time) VALUES " +
                "( :pollStartTime, :poolEndTime)")
                .setParameter("pollStartTime", internal.getPollingStartTime())
                .setParameter("poolEndTime", internal.getPollingEndTime());
        nativeQuery.executeUpdate();
    }

    @Override
    public PollingTimeInterval getPollingInterval() {
        return (PollingTimeInterval) em.createNativeQuery(
                "SELECT poll_start_time, poll_end_time FROM voting_config",
                PollingTimeInterval.class
        ).getSingleResult();
    }

    @Override
    public void savePoll(Poll poll) {
        reliablePersist(poll);
    }

    @Override
    public Poll getPollAndEmptyPollItems(String id) {
        return em.createQuery("SELECT p FROM Poll p JOIN FETCH PollItem WHERE p.id= ?1", Poll.class)
                .setParameter(1, id)
                .getSingleResult();
    }

    @Override
    public Vote getVoteInPoll(String voterId, String pollId) {
        return em.createQuery("SELECT v FROM Vote v WHERE v.pollId= :pollId AND v.voterId= :voterId", Vote.class)
                .setParameter("pollId", pollId)
                .setParameter("voterId", voterId)
                .getSingleResult();
    }

    @Override
    public PollItem getPollItem(final String pollId, final String pollItemId) {
        return em.createQuery("SELECT pi FROM PollItem pi JOIN FETCH LunchPlace lp JOIN FETCH lp.phones " +
        "                               WHERE pi.id= :pollItemId AND pi.poll.id= :pollId", PollItem.class)
                .setParameter("pollItemId", pollItemId)
                .setParameter("pollId", pollId).getSingleResult();
    }

    @Override
    public PollItem getPollItemWithPoll(String pollId, String pollItemId) {
        return em.createQuery("SELECT pi FROM PollItem pi JOIN FETCH Poll WHERE pi.id= :pollItemId AND pi.poll.id= :pollId", PollItem.class)
                .setParameter("pollItemId", pollItemId)
                .setParameter("pollId", pollId).getSingleResult();
    }

    @Override
    public void saveVote(final Vote vote) {
        reliablePersist(vote);
    }

    @Override
    public void removeVote(final Vote vote) {
        em.remove(vote);
    }
}
