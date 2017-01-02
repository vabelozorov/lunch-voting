package ua.belozorov.lunchvoting.repository.lunchplace;

import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Repository
public class LunchPlaceRepositoryImpl extends BaseRepository implements LunchPlaceRepository {

    private final CrudLunchPlaceRepository repository;

    @Autowired
    public LunchPlaceRepositoryImpl(CrudLunchPlaceRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(LunchPlace place) {
        reliablePersist(place);
    }

    @Override
    public void update(LunchPlace place, String userId) {
        LunchPlace saved = ofNullable(get(place.getId(), userId))
                                .orElseThrow(() -> new NotFoundException(place));
        saved = LunchPlace.builder(saved)
                .name(place.getName())
                .address(place.getAddress())
                .description(place.getDescription())
                .phones(place.getPhones())
                .build();
        em.merge(saved);
    }

    @Override
    public LunchPlace get(String id, String userId) {
        return repository.get(id, userId);
    }

    @Override
    public Collection<LunchPlace> getAll(String userId) {
        return repository.findAll(userId);
    }

    @Override
    public Collection<LunchPlace> getMultiple(Collection<String> placeIds) {
        String sql = "SELECT lp FROM LunchPlace lp WHERE lp.id IN :placeIds ORDER BY lp.name";
        return em.createQuery(sql, LunchPlace.class)
            .setParameter("placeIds", placeIds)
            .getResultList();
    }

    @Override
    public List<LunchPlace> getWithMenu(Collection<String> placeIds, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT DISTINCT lp FROM LunchPlace lp " +
                "LEFT JOIN FETCH lp.menus menus " +
                "LEFT JOIN FETCH menus.dishes";

        boolean addedWhere = false;
        if (startDate != null || endDate != null) {
            sql += (addedWhere ? " AND" : " WHERE") + " (menus.effectiveDate BETWEEN :startDate AND :endDate)";
            addedWhere = true;
        }
        if ( ! placeIds.isEmpty()) {
            sql += (addedWhere ? " AND" : " WHERE") + " lp.id IN :placeIds";
            addedWhere = true;
        }
        sql += " ORDER BY lp.name";

        TypedQuery<LunchPlace> query = em.createQuery(sql, LunchPlace.class)
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
    public boolean delete(String id, String userId) {
        em.createQuery("UPDATE PollItem pi SET pi.item = null WHERE pi.item.id = ?1")
                .setParameter(1, id)
                .executeUpdate();
        return repository.deleteById(id, userId) != 0;
    }

    @Override
    public List<LunchPlace> getByMenusForDate(final LocalDate date) {
        return repository.getByMenusForDate(date);
    }

    /**
     * <h2>Spring Data JPA repository</h2>
     *
     * @author vabelozorov on 21.11.16.
     */
    public interface CrudLunchPlaceRepository extends JpaRepository<LunchPlace, String> {

        @Query("SELECT lp FROM LunchPlace lp WHERE lp.id= :id and lp.adminId= :userId")
        LunchPlace get(@Param("id") String id, @Param("userId") String userId);

        @Query("SELECT DISTINCT lp FROM LunchPlace lp WHERE lp.adminId= ?1 ORDER BY lp.name ASC")
        List<LunchPlace> findAll(String userId);

        @Modifying
        @Query("DELETE FROM LunchPlace lp WHERE lp.id= :id AND lp.adminId= :userId")
        int deleteById(@Param("id") String id, @Param("userId") String userId);

        @Query("SELECT lp FROM LunchPlace lp WHERE lp.id= :id AND lp.adminId= :userId")
        LunchPlace getOne(@Param("id") String id, @Param("userId") String userId);

        @Query("SELECT lp FROM LunchPlace lp INNER JOIN Menu m WHERE m.effectiveDate= ?1")
        List<LunchPlace> getByMenusForDate(LocalDate date);
    }
}
