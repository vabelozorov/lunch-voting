package ua.belozorov.lunchvoting.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 08.02.17.
 */
@Entity
@Table(name = "domains")
public final class EatingDomain extends AbstractPersistableObject {

    @NotBlank
    @SafeHtml
    private final String name;
}
