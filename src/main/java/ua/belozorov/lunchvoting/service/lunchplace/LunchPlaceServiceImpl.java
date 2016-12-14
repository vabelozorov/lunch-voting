package ua.belozorov.lunchvoting.service.lunchplace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.User;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.to.MenuTo;

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
    private LunchPlaceRepository lunchPlaceRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    @Transactional
    public String create(LunchPlace place, User user) {
        lunchPlaceRepository.save(place);
        return place.getId();
    }

    @Override
    @Transactional
    public void update(LunchPlace place, User user) {
        lunchPlaceRepository.update(place, user.getId());
    }

    @Override
    public LunchPlace get(String id, User user) {
        return ofNullable(lunchPlaceRepository.get(id, user.getId()))
                .orElseThrow(() -> new NotFoundException(id, LunchPlace.class));
    }

    @Override
    public Collection<LunchPlace> getAll(User user) {
        return lunchPlaceRepository.getAll(user.getId());
    }

    @Override
    @Transactional
    public void delete(String id, User user) {
        if ( ! lunchPlaceRepository.delete(id, user.getId())) {
            throw new NotFoundException(id, LunchPlace.class);
        }
    }

    @Override
    @Transactional
    public String addMenu(String lunchPlaceId, MenuTo menuTo, User user) {
        LunchPlace place = ofNullable(lunchPlaceRepository.get(lunchPlaceId, user.getId()))
                                .orElseThrow(() -> new NotFoundException(lunchPlaceId, LunchPlace.class));
        Menu created = place.createMenu(menuTo.getEffectiveDate(), menuTo.getDishes());
        menuRepository.save(created);
        return created.getId();
    }

    @Override
    @Transactional
    public void deleteMenu(String lunchPlaceId, String menuId, User user) {
        LunchPlace place = ofNullable(lunchPlaceRepository.get(lunchPlaceId, user.getId()))
                .orElseThrow(() -> new NotFoundException(lunchPlaceId, LunchPlace.class));
        menuRepository.deleteMenu(place.getId(), menuId);
    }
}
