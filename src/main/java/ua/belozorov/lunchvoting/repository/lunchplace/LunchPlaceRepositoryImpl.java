package ua.belozorov.lunchvoting.repository.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.BaseRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @Autowired
    private CrudLunchPlaceRepository repository;

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

        @EntityGraph(attributePaths = {"menus"})
        @Query("SELECT lp FROM LunchPlace lp WHERE lp.id= :id and lp.adminId= :userId")
        LunchPlace getWithMenus(@Param("id") String id, @Param("userId") String userId);

        @Query("SELECT lp FROM LunchPlace lp WHERE lp.id= :id AND lp.adminId= :userId")
        LunchPlace getOne(@Param("id") String id, @Param("userId") String userId);

        @Query("SELECT lp FROM LunchPlace lp INNER JOIN Menu m WHERE m.effectiveDate= ?1")
        List<LunchPlace> getByMenusForDate(LocalDate date);
    }
}
