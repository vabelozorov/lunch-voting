package ua.belozorov.lunchvoting.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class BaseRepository {

    @PersistenceContext
    protected EntityManager em;

    public  <T> void reliablePersist(T persistable) {
        em.persist(persistable);
        em.flush();;
        em.detach(persistable);
    }

    public <T> T nullOrFirst(List<T> collection) {
        return collection.isEmpty() ? null : collection.get(0);
    }
}
