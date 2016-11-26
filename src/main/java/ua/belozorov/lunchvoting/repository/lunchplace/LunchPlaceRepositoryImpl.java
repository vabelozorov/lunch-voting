package ua.belozorov.lunchvoting.repository.lunchplace;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Repository
public class LunchPlaceRepositoryImpl implements LunchPlaceRepository {

    @Autowired
    private CrudLunchPlaceRepository repository;

    @PersistenceContext
    private EntityManager em;


    @Override
    public LunchPlace save(LunchPlace place, String userId) {
        final User user = em.getReference(User.class, userId);
        place.setAdmin(user);
        em.persist(place);
        /*
        Sorting for some object fields (Collection) occur when fetching data from db.
        If only return just persisted object, its fields won't be sorted.
        So it's necessary to flush changes and obtain a refreshed instance from DB,
        clearing session in between, otherwise the object will be fetched from session cache and not from DB.
        EntityManager.refresh is also an option, but it fetches a collection via 2nd SELECT and not via JOIN
         */
        em.flush();
        em.unwrap(Session.class).clear();
        return get(place.getId(), userId);
    }

    @Override
    public LunchPlace update(LunchPlace place, String userId) {
        LunchPlace saved = ofNullable(get(place.getId(), userId))
                                .orElseThrow(() -> new NotFoundException(place));
        saved = LunchPlace.builder(saved)
                .name(place.getName())
                .address(place.getAddress())
                .description(place.getDescription())
                .phones(place.getPhones()).build();
        return em.merge(saved);
        /*
        This unfortunately produces some weird UPDATE cross join syntactically wrong SQL statement (check CrudRepo)
         */
//        return repository.update(place.getName(), place.getAddress(), place.getDescription(), place.getPhones(),
//                place.getId(), userId) != 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
