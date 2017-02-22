package ua.belozorov.lunchvoting.repository.voting;

import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.voting.polling.LunchPlacePoll;
import ua.belozorov.lunchvoting.model.voting.polling.Vote;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.belozorov.lunchvoting.util.Pair.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 30.11.16.
 */
@Repository
public class PollRepositoryImpl extends BaseRepository implements PollRepository {

    @Override
    public LunchPlacePoll save(LunchPlacePoll poll) {
        em.persist(poll);
        return poll;
    }

    @Override
    public LunchPlacePoll get(String pollId) {
        String sql = "SELECT p FROM LunchPlacePoll p " +
                "WHERE p.id= :id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", pollId)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll getWithPollItemsAndVotes(String areaId, String pollId) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "LEFT JOIN FETCH p.votes v " +
                "WHERE p.id= :id " +
                    "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("id", pollId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll getWithPollItems(String areaId, String pollId) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE p.id= :id " +
                    "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("id", pollId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll get(String areaId, String pollId) {
        String sql = "SELECT p FROM LunchPlacePoll p " +
                "WHERE p.id= :pollId AND p IN (" +
                    "SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("pollId", pollId)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public LunchPlacePoll getWithPollItems(String pollId) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "LEFT JOIN FETCH p.pollItems pi " +
                "WHERE p.id= :id ";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("id", pollId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        em.clear();
        return super.nullOrFirst(polls);
    }

    @Override
    public List<LunchPlacePoll> getAll(String areaId) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "WHERE p IN (" +
                    "SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId) " +
                "ORDER BY p.menuDate DESC, p.id";
        return em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    @Override
    public List<LunchPlacePoll> getPollByActivePeriod(String areaId, LocalDateTime startDt, LocalDateTime endDt) {
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "WHERE (:startDt BETWEEN p.timeConstraint.startTime AND p.timeConstraint.endTime "+
                    "OR :endDt BETWEEN p.timeConstraint.startTime AND p.timeConstraint.endTime) " +
                    "AND p IN (" +
                        "SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId) " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("startDt", startDt)
                .setParameter("endDt", endDt)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public List<LunchPlacePoll> getFuturePolls(String areaId) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "WHERE p.timeConstraint.startTime > :now " +
                "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId) " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("now", now)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public List<LunchPlacePoll> getPastPolls(String areaId) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "SELECT DISTINCT p FROM LunchPlacePoll p " +
                "WHERE p.timeConstraint.endTime < :now " +
                    "AND  p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId) " +
                "ORDER BY p.menuDate DESC, p.id";
        List<LunchPlacePoll> polls = em.createQuery(sql, LunchPlacePoll.class)
                .setParameter("areaId", areaId)
                .setParameter("now", now)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        return polls;
    }

    @Override
    public Boolean isActive(String areaId, String pollId) {
        String sql = "SELECT CASE WHEN count(p) > 0 THEN false ELSE true END " +
                "FROM LunchPlacePoll p  " +
                "WHERE p.timeConstraint.endTime < :now AND p.id= :id " +
                    "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        return em.createQuery(sql, Boolean.class)
                .setParameter("areaId", areaId)
                .setParameter("id", pollId)
                .setParameter("now", LocalDateTime.now())
                .getSingleResult();
    }

    @Override
    public List<String> getVotedByVoter(String areaId, String pollId, String voterId) {
        String sql = "SELECT vi.pollItem.id FROM LunchPlacePoll p " +
                "INNER JOIN p.votes vi " +
                "WHERE p.id= :pollId AND vi.voterId= :voterId " +
                    "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        return super.regularGetList(sql, String.class,
                pairOf("areaId", areaId), pairOf("pollId", pollId), pairOf("voterId", voterId));
    }

    @Override
    public boolean removePoll(String areaId, String pollId) {
        String sql = "DELETE FROM LunchPlacePoll p " +
                "WHERE p.id= :id AND p IN (" +
                "SELECT po FROM EatingArea ea JOIN ea.polls po WHERE ea.id= :areaId)";
        return em.createQuery(sql)
                .setParameter("areaId", areaId)
                .setParameter("id", pollId)
                .executeUpdate() != 0;
    }

    @Override
    public Vote save(Vote vote) {
        em.persist(vote);
        return vote;
    }

    @Override
    public Vote getVote(String voterId, String voteId) {
        String sql = "SELECT v FROM Vote v WHERE v.id= :voteId AND v.voterId= :voterId";
        return regularGet(sql, Vote.class,
                pairOf("voterId", voterId), pairOf("voteId", voteId));
    }

    @Override
    public Vote getFullVote(String voterId, String voteId) {
        String sql = "SELECT v FROM Vote v " +
                "INNER JOIN FETCH v.poll p " +
                "INNER JOIN FETCH v.pollItem " +
                "WHERE v.id= :voteId AND v.voterId= :voterId";
        return regularGet(sql, Vote.class,
                pairOf("voterId", voterId), pairOf("voteId", voteId));
    }

    @Override
    public Vote getFullVoteInPoll(String areaId, String voterId, String pollId) {
        String sql = "SELECT v FROM Vote v " +
                "INNER JOIN FETCH v.poll p " +
                "INNER JOIN FETCH v.pollItem pi " +
                "WHERE p.id= :pollId AND v.voterId= :voterId " +
                    "AND p IN (SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId)";
        return super.regularGet(sql, Vote.class,
                pairOf("areaId", areaId), pairOf("pollId", pollId), pairOf("voterId", voterId));
    }

    @Override
    public List<Vote> getFullVotesForPoll(String areaId, String pollId) {
        String sql = "SELECT v FROM Vote v " +
                "INNER JOIN FETCH v.poll p " +
                "INNER JOIN FETCH v.pollItem pi " +
                "WHERE p.id= :pollId AND p IN (" +
                    "SELECT p1 FROM EatingArea ea JOIN ea.polls p1 WHERE ea.id= :areaId) " +
                "ORDER BY v.voteTime, v.id";
        return super.regularGetList(sql, Vote.class,
                pairOf("areaId", areaId), pairOf("pollId", pollId));
    }

    @Override
    public void remove(Vote vote) {
        em.remove(vote);
    }

    @Override
    public void remove(Set<Vote> forRemoval) {
        List<String> ids = forRemoval.stream().map(Vote::getId).collect(Collectors.toList());
        em.createQuery("DELETE FROM Vote v WHERE v.id IN :ids")
                .setParameter("ids", ids).executeUpdate();
    }
}
