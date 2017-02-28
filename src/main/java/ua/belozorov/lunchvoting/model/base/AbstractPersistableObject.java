package ua.belozorov.lunchvoting.model.base;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 *
 */
@MappedSuperclass
public abstract class AbstractPersistableObject implements Persistable {

    @Id
    protected final String id;

    @Version
    protected final Integer version;

    public AbstractPersistableObject() {
        this(IdGenerator.createId(), null);
    }

    public AbstractPersistableObject(String id, Integer version) {
        this.id = id == null ? IdGenerator.createId() : id;
        this.version = version;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.id == null) return false;
        if (!(o instanceof AbstractPersistableObject)) return false;

        AbstractPersistableObject that = (AbstractPersistableObject) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
