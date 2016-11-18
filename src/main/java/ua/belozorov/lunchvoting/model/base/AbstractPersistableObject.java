package ua.belozorov.lunchvoting.model.base;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * <h1> A base class for JPA entities</h1>
 * <p/>
 * An ID for any subclass instance is always supplied at creation time, thus making it impossible to have an instance without id.
 * The state (persisted|not persisted) can be determined by means of {@code version} field via @{code #isNew} method which is expected to be managed by a JPA provider.
 * For this purpose, this field must be persisted to a database, so a corresponding database column must be available.
 * It also follows that the JPA provider does not manage an ID generation.
 * Such approach allows to rely on a simple implementation of {@code equals} and {@code hashcode} which include only the entity id.
 * <p/>
 * The following use of the class is intended:
 * <ol>
 *     <li>An app subclasses this class or implements {@link Persistable} interface</li>
 *     <li>The app creates an instance of the subclass (a JPA entity). This instance has an auto-generated String id (refer to {@link IdGenerator}) and
 *     the {@code version} field equals to {@code null}. The method @{code isNew} returns true</li>
 *     <li>The entity is persisted. The JPA provider is responsible for assigning a value.to the version field </li>
 *     <li>During the process of retrieving the entity from a database, the JPA provider reflectively creates a empty entity which has all traits of a new entity
 *     Then the provider invokes {@code setId} method and replaces a the entity's id for its actual id from a database</li>
 * </ol>
 * @author vabelozorov on 09.10.16.
 * @see IdGenerator
 */
@MappedSuperclass
public class AbstractPersistableObject implements Persistable {

    /**
     * Is used as an ID for a JPA entity.
     * A JPA provider is expected to invoke @{code setId} method to set an ID when retrieving an entity from a database.
     * The JPA provider must not manage ID creation.
     */
    @Id
    protected String id = IdGenerator.createId();

    /**
     * Is used to determine new|saved state of an entity
     */
    @Version
    protected Integer version;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        if (id != null) {
            this.id = id;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.id == null) return false;
        if (!(o instanceof AbstractPersistableObject)) return false;

        AbstractPersistableObject that = (AbstractPersistableObject) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }


    @Override
    public boolean isNew() {
        return version == null;
    }
}
