package ua.belozorov.lunchvoting.repository.voting;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.model.voting.PollItem;
import ua.belozorov.lunchvoting.model.voting.PollingTimeInterval;
import ua.belozorov.lunchvoting.model.voting.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import javax.persistence.EntityGraph;
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
        return em.createQuery("SELECT p FROM Poll p JOIN FETCH PollItem pi WHERE p.id= ?1", Poll.class)
                .setParameter(1, id)
                .getSingleResult();
    }

    @Override
    public Vote getVoteInPoll(String voterId, String pollId) {
        return em.createQuery("SELECT v FROM Vote v WHERE v.poll.id= :pollId AND v.voterId= :voterId", Vote.class)
                .setParameter("pollId", pollId)
                .setParameter("voterId", voterId)
                .getSingleResult();
    }

    @Override
    public PollItem getPollItem(final String pollId, final String pollItemId) {
        return em.createQuery("SELECT pi FROM PollItem pi JOIN FETCH LunchPlace lp " +
                "WHERE pi.id= :pollItemId AND pi.poll.id= :pollId", PollItem.class)
                .setParameter("pollItemId", pollItemId)
                .setParameter("pollId", pollId).getSingleResult();

    }

    @Override
    @Transactional
    public Poll getPollAndPollItem(String pollId, String pollItemId) {

        Poll poll = em.createQuery("SELECT p FROM Poll p " +
                "JOIN FETCH p.pollItems pi " +
                "JOIN FETCH pi.item i " +
                "JOIN FETCH i.menus m " +
                "JOIN FETCH m.dishes " +
                "WHERE p.id= :pollId AND pi.id = :pollItemId", Poll.class)
                .setParameter("pollItemId", pollItemId)
                .setParameter("pollId", pollId).getSingleResult();
        em.clear();
        return poll;
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
