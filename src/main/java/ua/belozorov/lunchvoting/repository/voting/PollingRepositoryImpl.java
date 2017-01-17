package ua.belozorov.lunchvoting.repository.voting;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.voting.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.PollItem;
import ua.belozorov.lunchvoting.model.voting.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
@Repository
public class PollingRepositoryImpl extends BaseRepository implements PollingRepository {

    @Override
    public void savePoll(LunchPlacePoll poll) {
        super.reliablePersist(poll);
    }

    @Override
    public LunchPlacePoll getPollWithVotesAndEmptyPollItems(String id) {
        List<LunchPlacePoll> polls = em.createQuery(
                "SELECT p FROM LunchPlacePoll p " +
                        "LEFT JOIN FETCH p.pollItems " +
                        "LEFT JOIN FETCH p.votes " +
                        "WHERE p.id= ?1", LunchPlacePoll.class)
                .setParameter(1, id)
                .getResultList();
        return super.nullOrFirst(polls);
    }

    @Override
    public Vote getVoteInPoll(String voterId, String pollId) {
        List<Vote> votes = em.createQuery("SELECT v FROM Vote v WHERE v.poll.id= :pollId AND v.voterId= :voterId", Vote.class)
                .setParameter("pollId", pollId)
                .setParameter("voterId", voterId)
                .getResultList();
        return super.nullOrFirst(votes);
    }

    @Override
    public PollItem getPollItem(final String pollId, final String pollItemId) {
        List<PollItem> items = em.createQuery("SELECT pi FROM PollItem pi JOIN FETCH LunchPlace lp " +
                "WHERE pi.id= :pollItemId AND pi.poll.id= :pollId", PollItem.class)
                .setParameter("pollItemId", pollItemId)
                .setParameter("pollId", pollId)
                .getResultList();
        return super.nullOrFirst(items);
    }

    @Override
    @Transactional
    public LunchPlacePoll getPollAndPollItem(String pollId, Collection<String> pollItemIds) {
        if (pollItemIds.size() < 1) {
            throw new IllegalStateException("PollItemIds size must be greater than 0");
        }
        List<LunchPlacePoll> polls = em.createQuery("SELECT p FROM LunchPlacePoll p " +
                "INNER JOIN FETCH p.pollItems pi " +
                "INNER JOIN FETCH pi.item i " +
                "INNER JOIN FETCH i.menus m " +
                "INNER JOIN FETCH m.dishes " +
                "WHERE p.id= :pollId AND pi.id IN :pollItemIds", LunchPlacePoll.class)
                .setParameter("pollItemIds", pollItemIds)
                .setParameter("pollId", pollId)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll getPollAndPollItem(String pollId, String pollItemId) {
        return this.getPollAndPollItem(pollId, Collections.singletonList(pollItemId));
    }

    @Override
    public LunchPlacePoll getFullPoll(String pollId) {
        List<LunchPlacePoll> polls = em.createQuery("SELECT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "LEFT JOIN FETCH pi.item i " +
                "LEFT JOIN FETCH i.menus m " +
                "LEFT JOIN FETCH m.dishes " +
                "WHERE p.id= :pollId AND m.effectiveDate= p.menuDate", LunchPlacePoll.class)
                .setParameter("pollId", pollId)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public void saveVote(final Vote vote) {
        super.reliablePersist(vote);
    }

    @Override
    public void removeVote(final Vote vote) {
        em.remove(vote);
    }
}
