package ua.belozorov.lunchvoting.repository.lunchplace;

import org.hibernate.Hibernate;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.lunchplace.EatingArea;
import ua.belozorov.lunchvoting.model.lunchplace.JoinAreaRequest;
import ua.belozorov.lunchvoting.repository.BaseRepository;
import ua.belozorov.lunchvoting.to.AreaTo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**

 *
 * Created on 08.02.17.
 */
@Repository
public class EatingAreaRepositoryImpl extends BaseRepository implements EatingAreaRepository {

    @Override
    public EatingArea save(EatingArea domain) {
        super.reliablePersist(domain);
        return domain;
    }

    @Override
    public boolean delete(String areaId) {
        return em.createQuery("DELETE FROM EatingArea ea WHERE ea.id= :id")
                    .setParameter("id", areaId)
                    .executeUpdate() != 0;
    }

    @Override
    public EatingArea getArea(String areaId, Fields... fields) {
        List<EatingArea> areas = em.createQuery("SELECT ea FROM EatingArea ea " +
                "WHERE ea.id= :id", EatingArea.class)
                .setParameter("id", areaId).getResultList();
        EatingArea area = super.nullOrFirst(areas);
        new HashSet<>(Arrays.asList(fields)).forEach(
                f -> Hibernate.initialize(f.getProxyObject(area))
        );
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaQuery<EatingArea> criteria = builder.createQuery(EatingArea.class);
//        Root<EatingArea> ea = criteria.from(EatingArea.class);
//        new HashSet<>(Arrays.asList(fields)).forEach(f -> ea.fetch(f.getField(), JoinType.LEFT));
//        criteria.select(ea).where(builder.equal(ea.<String>get("id"), areaId));
//        EatingArea area = super.nullOrFirst(em.createQuery(criteria).getResultList());
        return area;
    }

    @Override
    public AreaTo getAreaTo(String areaId) {
        String sql = "SELECT new list(ea.id, ea.name, ea.created, u.id, pl.id, po.id) " +
                "FROM EatingArea ea " +
                "LEFT JOIN ea.users u " +
                "LEFT JOIN ea.places pl " +
                "LEFT JOIN ea.polls po " +
                "WHERE ea.id= :id ";
        List<List> resultList = em.createQuery(sql, List.class).setParameter("id", areaId).getResultList();
        AreaTo to = null;
        Set<String> userIds = new TreeSet<>();
        Set<String> pollIds= new TreeSet<>();
        Set<String> placeIds = new TreeSet<>();
        if ( ! resultList.isEmpty()) {
            for (List inner : resultList) {
                userIds.add((String)inner.get(3));
                placeIds.add((String)inner.get(4));
                pollIds.add((String)inner.get(5));
            }
            List firstRow = resultList.get(0);
            to = new AreaTo((String)firstRow.get(0), (String)firstRow.get(1), (LocalDateTime)firstRow.get(2),
                    new ArrayList<>(userIds), new ArrayList<>(pollIds), new ArrayList<>(placeIds));
        }
        return to;
    }

    @Override
    public AreaTo getAreaToSummary(String areaId) {
        String sql = "SELECT new ua.belozorov.lunchvoting.to.AreaTo(ea.id, ea.name, ea.created, count(DISTINCT u), count(DISTINCT pl), count(DISTINCT po)) " +
                "FROM EatingArea ea " +
                "LEFT JOIN ea.users u " +
                "LEFT JOIN ea.places pl " +
                "LEFT JOIN ea.polls po " +
                "WHERE ea.id= :id " +
                "GROUP BY ea";
        return super.nullOrFirst(em.createQuery(sql, AreaTo.class).setParameter("id", areaId).getResultList());
    }

    @Override
    public JoinAreaRequest getJoinRequest(String areaId, String requestId) {
        String sql = "SELECT jar FROM JoinAreaRequest jar " +
                "INNER JOIN FETCH jar.area " +
                "INNER JOIN FETCH jar.requester " +
                "WHERE jar.id= :requestId AND jar.area.id= :areaId";
        return super.nullOrFirst(
                em.createQuery(sql, JoinAreaRequest.class)
                .setParameter("requestId", requestId)
                .setParameter("areaId", areaId)
                .getResultList()
        );
    }

    @Override
    public List<EatingArea> getByNameStarts(String search) {
        String sql = "SELECT ea FROM EatingArea ea " +
                "WHERE ea.name LIKE :search " +
                "ORDER BY ea.name";
        return em.createQuery(sql, EatingArea.class)
                .setParameter("search", search + "%")
                .getResultList();

    }

    @Override
    public JoinAreaRequest save(JoinAreaRequest request) {
        super.reliablePersist(request);
        return request;
    }

    @Override
    public JoinAreaRequest update(JoinAreaRequest request) {
        return em.merge(request);
    }

    @Override
    public EatingArea update(EatingArea area) {
        return em.merge(area);
    }

    @Override
    public List<JoinAreaRequest> getJoinRequestsByStatus(String areaId, JoinAreaRequest.JoinStatus status) {
        String sql = "SELECT jar FROM JoinAreaRequest jar " +
                "INNER JOIN FETCH jar.area " +
                "INNER JOIN FETCH jar.requester " +
                "WHERE jar.status= :status AND jar.area.id= :areaId " +
                "ORDER BY jar.created DESC";
        return em.createQuery(sql, JoinAreaRequest.class)
                .setParameter("areaId", areaId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<JoinAreaRequest> getJoinRequestsByRequester(String requesterId) {
        String sql = "SELECT jar FROM JoinAreaRequest jar " +
                "INNER JOIN FETCH jar.area " +
                "INNER JOIN FETCH jar.requester " +
                "WHERE jar.requester.id= :requesterId " +
                "ORDER BY jar.created DESC";
        return em.createQuery(sql, JoinAreaRequest.class)
                .setParameter("requesterId", requesterId)
                .getResultList();
    }

    @Override
    public JoinAreaRequest getJoinRequestByRequester(String requesterId, String requestId) {
        String sql = "SELECT jar FROM JoinAreaRequest jar " +
                "INNER JOIN FETCH jar.area " +
                "INNER JOIN FETCH jar.requester " +
                "WHERE jar.id= :requestId AND jar.requester.id= :requesterId";
        return super.nullOrFirst(em.createQuery(sql, JoinAreaRequest.class)
                .setParameter("requestId", requestId)
                .setParameter("requesterId", requesterId)
                .getResultList());
    }

    public enum Fields {
        USERS(EatingArea::getUsers),
        POLLS(EatingArea::getPolls),
        PLACES(EatingArea::getPlaces);

        private final Function<EatingArea, Object> valueFunction;

        Fields(Function<EatingArea, Object> valueFunction) {
            this.valueFunction = valueFunction;
        }

        public Object getProxyObject(@Nullable EatingArea ea) {
            return ea == null ? null : valueFunction.apply(ea);
        }
    }
}
