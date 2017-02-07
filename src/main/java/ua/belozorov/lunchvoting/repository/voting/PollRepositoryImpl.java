package ua.belozorov.lunchvoting.repository.voting;

import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
@Repository
public class PollRepositoryImpl extends BaseRepository implements PollRepository {

    @Override
    public void savePoll(LunchPlacePoll poll) {
        super.reliablePersist(poll);
    }

    @Override
    public boolean removePoll(String id) {
        return em.createQuery("DELETE FROM LunchPlacePoll p WHERE p.id= :id")
                .setParameter("id", id).executeUpdate() != 0;
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

    @Override
    public LunchPlacePoll getPollAndPollItemsAndVotes(String pollId) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "LEFT JOIN FETCH p.votes v " +
                "WHERE p.id= :id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", pollId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll get(String id) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE p.id= :id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", id)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll getWithVotes(String id) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "LEFT JOIN FETCH p.votes " +
                "WHERE p.id= :id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", id)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public List<LunchPlacePoll> getAllPolls() {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "ORDER BY p.menuDate DESC, p.id";
        return em.createQuery(sql, LunchPlacePoll.class)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<LunchPlacePoll> getPollByActivePeriod(LocalDateTime startDt, LocalDateTime endDt) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE :startDt BETWEEN p.timeConstraint.startTime AND p.timeConstraint.endTime "+
                    "OR :endDt BETWEEN p.timeConstraint.startTime AND p.timeConstraint.endTime " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("startDt", startDt)
                .setParameter("endDt", endDt)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public List<LunchPlacePoll> getFuturePolls() {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE p.timeConstraint.startTime > :now " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("now", now)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public List<LunchPlacePoll> getPastPolls() {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE p.timeConstraint.endTime < :now " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("now", now)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public Collection<Vote> getVotesForPoll(String pollId) {
        String sql = "SELECT v FROM Vote v WHERE v.poll.id= :pollId";
        List<Vote> votes = em.createQuery(sql, Vote.class)
                .setParameter("pollId", pollId)
                .getResultList();
        return votes;
    }

    @Override
    public Boolean isActive(String id) {
        String sql = "SELECT CASE WHEN count(p) > 0 THEN false ELSE true END " +
                "FROM LunchPlacePoll p  " +
                "WHERE p.timeConstraint.endTime < :now AND p.id= :id";
        return em.createQuery(sql, Boolean.class)
                .setParameter("id", id)
                .setParameter("now", LocalDateTime.now())
                .getSingleResult();
    }
}
