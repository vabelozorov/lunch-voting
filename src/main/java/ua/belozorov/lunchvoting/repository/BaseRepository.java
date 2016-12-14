package ua.belozorov.lunchvoting.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 02.12.16.
 */
public abstract class BaseRepository {

    @PersistenceContext
    protected EntityManager em;

    protected <T> void reliablePersist(T persistable) {
        em.persist(persistable);
        em.flush();;
        em.detach(persistable);
    }
}
