package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.ILunchPlaceRepository;

import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Service
@Transactional(readOnly = true)
public class LunchPlaceService implements ILunchPlaceService {

    @Autowired
    private ILunchPlaceRepository repository;

    @Override
    @Transactional
    public LunchPlace create(LunchPlace place, String userId) {
        return repository.save(place, userId);
    }

    @Override
    @Transactional
    public void update(LunchPlace place, String userId) {
        repository.update(place, userId);
    }

    @Override
    public LunchPlace get(String id, String userId) {
        return ofNullable(repository.get(id, userId))
                .orElseThrow(() -> new NotFoundException(id, LunchPlace.class));
    }

    @Override
    public Collection<LunchPlace> getAll(String userId) {
        return repository.getAll(userId);
    }

    @Override
    @Transactional
    public void delete(String id, String userId) {
        if ( ! repository.delete(id, userId)) {
            throw new NotFoundException(id, LunchPlace.class);
        }
    }
}
