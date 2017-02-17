package ua.belozorov.lunchvoting.util.hibernate;

import ua.belozorov.lunchvoting.model.UserRole;

import javax.persistence.AttributeConverter;
import java.util.Set;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
public class RolesToIntegerConverter implements AttributeConverter<Set<UserRole>, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Set<UserRole> attribute) {
        return UserRole.rolesToBitmask(attribute);
    }

    @Override
    public Set<UserRole> convertToEntityAttribute(Integer dbData) {
        return UserRole.toUserRoles(dbData);
    }
}
