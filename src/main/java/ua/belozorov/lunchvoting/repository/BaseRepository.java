package ua.belozorov.lunchvoting.repository;

import ua.belozorov.lunchvoting.util.Pair;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 *
 * Created on 02.12.16.
 */
public abstract class BaseRepository {

    @PersistenceContext
    protected EntityManager em;

    public  <T> T reliablePersist(T persistable) {
        em.persist(persistable);
        return persistable;
//        em.flush();;
//        em.detach(persistable);
    }

    public <T> T nullOrFirst(List<T> collection) {
        return collection.isEmpty() ? null : collection.get(0);
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }

    @SafeVarargs
    public final <T> T regularGet(String sql, Class<T> returnType, Pair<String, Object>... pairs) {
        TypedQuery<T> query = em.createQuery(sql, returnType);
        for (Pair<String, Object> pair : pairs) {
            query = query.setParameter(pair.getA(), pair.getB());
        }
        return nullOrFirst(query.getResultList());
    }

    @SafeVarargs
    public final <T> List<T> regularGetList(String sql, Class<T> returnType, Pair<String, Object>... pairs) {
        TypedQuery<T> query = em.createQuery(sql, returnType);
        for (Pair<String, Object> pair : pairs) {
            query = query.setParameter(pair.getA(), pair.getB());
        }
        return query.getResultList();
    }
}
