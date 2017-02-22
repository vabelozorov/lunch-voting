package ua.belozorov.lunchvoting.repository.lunchplace;

import org.hibernate.jpa.QueryHints;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.BaseRepository;
import ua.belozorov.lunchvoting.util.Pair;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static ua.belozorov.lunchvoting.util.Pair.*;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */

//TODO make delete with area in one request
@Repository
public class LunchPlaceRepositoryImpl extends BaseRepository implements LunchPlaceRepository {

    @Override
    public LunchPlace save(LunchPlace place) {
        reliablePersist(place);
        return place;
    }

    @Override
    public void update(LunchPlace place) {
        em.merge(place);
    }

    @Override
    public LunchPlace get(String placeId) {
        String sql = "SELECT lp FROM LunchPlace lp " +
                "WHERE lp.id= :placeId";
        return super.regularGet(sql, LunchPlace.class, pairOf("placeId", placeId));
    }

    @Override
    public LunchPlace get(String areaId, String placeId) {
        String sql = "SELECT lp FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "WHERE ea.id= :areaId AND lp.id= :placeId";
        return super.regularGet(sql, LunchPlace.class,
                pairOf("areaId", areaId), pairOf("placeId", placeId));
    }

    @Override
    public List<LunchPlace> getAll(String areaId) {
        String sql = "SELECT lp FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "WHERE ea.id= :areaId ORDER BY lp.name ASC";
        return em.createQuery(sql, LunchPlace.class).setParameter("areaId", areaId).getResultList();
    }

    @Override
    public List<LunchPlace> getMultiple(String areaId, Set<String> placeIds) {
        String sql = "SELECT lp FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "WHERE ea.id= :areaId AND lp.id IN :placeIds ORDER BY lp.name";
        return em.createQuery(sql, LunchPlace.class)
            .setParameter("areaId", areaId)
            .setParameter("placeIds", placeIds)
            .getResultList();
    }

    @Override
    //TODO try with Criteria API
    public List<LunchPlace> getWithMenu(String areaId, Set<String> placeIds, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT DISTINCT lp FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "LEFT JOIN FETCH lp.menus menus " +
                "LEFT JOIN FETCH menus.dishes " +
                "WHERE ea.id= :areaId";

        if (startDate != null || endDate != null) {
            sql += " AND (menus.effectiveDate BETWEEN :startDate AND :endDate)";
        }
        if ( ! placeIds.isEmpty()) {
            sql += " AND lp.id IN :placeIds";
        }
        sql += " ORDER BY lp.name";

        TypedQuery<LunchPlace> query = em.createQuery(sql, LunchPlace.class)
                .setParameter("areaId", areaId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        if ( ! placeIds.isEmpty()) {
            query.setParameter("placeIds", placeIds);
        }
        if (startDate != null || endDate != null) {
            query.setParameter("startDate", startDate == null ? LocalDate.ofEpochDay(0) : startDate);
            query.setParameter("endDate", endDate == null ? LocalDate.now() : endDate);
        }

        return query.getResultList();
    }

    @Override
    public boolean delete(String areaId, String placeId) {
        return em.createNativeQuery("DELETE FROM places WHERE places.area_id= :areaId AND places.id= :placeId")
                .setParameter("areaId", areaId)
                .setParameter("placeId", placeId).executeUpdate() != 0;
    }

    @Override
    public List<LunchPlace> getIfMenuForDate(String areaId, LocalDate date) {
        String sql = "SELECT DISTINCT lp FROM EatingArea ea " +
                "INNER JOIN ea.places lp " +
                "INNER JOIN FETCH lp.menus m " +
                "WHERE ea.id= :areaId AND m.effectiveDate= :date " +
                "ORDER BY lp.name";
        List<LunchPlace> places = em.createQuery(sql, LunchPlace.class)
                .setParameter("areaId", areaId)
                .setParameter("date", date)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
        em.clear();
        return places;
    }
}
