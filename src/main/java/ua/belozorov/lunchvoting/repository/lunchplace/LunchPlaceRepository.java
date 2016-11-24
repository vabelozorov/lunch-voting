package ua.belozorov.lunchvoting.repository.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Repository
public class LunchPlaceRepository implements ILunchPlaceRepository {

    @Autowired
    private CrudLunchPlaceRepository repository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public LunchPlace save(LunchPlace lunchPlace, String userId) {
        User userRef = em.getReference(User.class, userId);
//        lunchPlace.setAdmin(userRef);
        em.persist(lunchPlace);
        return lunchPlace;
    }

    @Override
    public boolean update(LunchPlace lunchPlace, String userId) {
        User userRef = em.getReference(User.class, userId);
//        lunchPlace.setAdmin(userRef);
        return em.merge(lunchPlace) != null;
    }

    @Override
    public LunchPlace get(String id, String userId) {
        return repository.getOne(id, userId);
    }

    @Override
    public Collection<LunchPlace> getAll(String userId) {
        return repository.findAll(userId);
    }

    @Override
    public boolean delete(String id, String userId) {
        return repository.deleteById(id, userId) != 0;
    }
}
