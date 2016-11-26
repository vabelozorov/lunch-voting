package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.BadSyntaxException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.transformers.LunchPlaceTransformer;

import java.util.Collection;

import static java.util.Optional.ofNullable;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 21.11.16.
 */
@Service
@Transactional(readOnly = true)
public class LunchPlaceServiceImpl implements LunchPlaceService {

    @Autowired
    private LunchPlaceRepository repository;

    @Override
    @Transactional
    public LunchPlaceTo create(LunchPlaceTo placeTo, User user) {
        LunchPlace place = LunchPlaceTransformer.toEntity(placeTo);
        LunchPlace created = repository.save(place, user.getId());
        return LunchPlaceTransformer.toDto(created);
    }

    @Override
    @Transactional
    public void update(LunchPlaceTo placeTo, User user) {
        LunchPlace place = LunchPlaceTransformer.toEntity(placeTo);
        repository.update(place, user.getId());
    }

    @Override
    public LunchPlaceTo get(String id, User user) {
        LunchPlace place = ofNullable(repository.get(id, user.getId()))
                .orElseThrow(() -> new NotFoundException(id, LunchPlace.class));
        return LunchPlaceTransformer.toDto(place);
    }

    @Override
    public Collection<LunchPlaceTo> getAll(User user) {
        Collection<LunchPlace> places = repository.getAll(user.getId());
        return LunchPlaceTransformer.collectionToDto(places);
    }

    @Override
    @Transactional
    public void delete(String id, User user) {
        if ( ! repository.delete(id, user.getId())) {
            throw new NotFoundException(id, LunchPlace.class);
        }
    }
}
