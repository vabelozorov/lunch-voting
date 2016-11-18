package ua.belozorov.lunchvoting.model.base;

/**
 * Created by vabelozorov on 09.10.16.
 */
public interface Persistable {
    String getId();

    /**
     * Sets the entity's id to its actual id
     * Expected to be invoked by a JPA provider after the entity has been fetched from a database
     * @param id entity id
     */
    void setId(String id);

    /**
     * Determines whether the entity is a new or has been saved to a database.
     * This field is managed by a JPA provider.
     * @return true if the entity has not been persisted by the JPA provider, false otherwise.
     */
    boolean isNew();
}
