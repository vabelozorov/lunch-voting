package ua.belozorov.lunchvoting.repository.voting;

import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Poll;
import ua.belozorov.lunchvoting.model.voting.polling.PollItem;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public LunchPlacePoll getPollAndPollItem(String pollId, Collection<String> pollItemIds) {
        if (pollItemIds.isEmpty()) {
            throw new IllegalStateException("PollItemIds must not be empty");
        }
        List<LunchPlacePoll> polls = em.createQuery("SELECT p FROM LunchPlacePoll p " +
                "INNER JOIN FETCH p.pollItems pi " +
                "INNER JOIN FETCH pi.item i " +
                "INNER JOIN FETCH i.menus m " +
                "LEFT JOIN FETCH m.dishes " +
                "WHERE p.id= :pollId AND pi.id IN :pollItemIds AND m.effectiveDate= p.menuDate", LunchPlacePoll.class)
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
        List<LunchPlacePoll> polls = em.createQuery("SELECT DISTINCT p FROM LunchPlacePoll p " +
                "INNER JOIN FETCH p.pollItems pi " +
                "INNER JOIN FETCH pi.item i " +
                "INNER JOIN FETCH i.menus m " +
                "LEFT JOIN FETCH m.dishes " +
                "LEFT JOIN FETCH p.votes " +
                "WHERE p.id= :pollId AND m.effectiveDate= p.menuDate", LunchPlacePoll.class)
                .setParameter("pollId", pollId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
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

    @Override
    public void removeVotes(Set<Vote> forRemoval) {
        List<String> ids = forRemoval.stream().map(Vote::getId).collect(Collectors.toList());
        em.createQuery("DELETE FROM Vote v WHERE v.id IN :ids")
                .setParameter("ids", ids).executeUpdate();
    }

    @Override
    public void replaceVote(final Set<Vote> forRemoval, final Vote acceptedVote) {
        this.removeVotes(forRemoval);
        this.saveVote(acceptedVote);
    }

    @Override
    public LunchPlacePoll getPollEmptyPollItemsAndVotes(String pollId) {
        String sql = "SELECT p FROM LunchPlacePoll p " +
                "INNER JOIN FETCH p.pollItems pi " +
                "LEFT JOIN FETCH p.votes " +
                "WHERE p.id= :id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", pollId).getResultList();
        return super.nullOrFirst(polls);
    }

    @Override
    public Collection<String> getVotedByVoter(String pollId, String voterId) {
        String sql = "SELECT vi.pollItem.id FROM LunchPlacePoll p " +
                "INNER JOIN p.votes vi " +
                "WHERE p.id= :pollId AND vi.voterId= :voterId";
        List<String> ids = em.createQuery(sql, String.class)
                .setParameter("pollId", pollId)
                .setParameter("voterId", voterId)
                .getResultList();
        return ids;
    }
}
