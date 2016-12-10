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
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.to.MenuTo;
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
    private LunchPlaceRepository lunchPlaceRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    @Transactional
    public LunchPlaceTo create(LunchPlaceTo placeTo, User user) {
        LunchPlace place = LunchPlaceTransformer.toEntity(placeTo, user.getId());
        lunchPlaceRepository.save(place);
        return LunchPlaceTransformer.toDto(lunchPlaceRepository.getWithPhones(place.getId(), place.getAdminId()));
    }

    @Override
    @Transactional
    public void update(LunchPlaceTo placeTo, User user) {
        LunchPlace place = LunchPlaceTransformer.toEntity(placeTo, user.getId());
        lunchPlaceRepository.update(place, user.getId());
    }

    @Override
    public LunchPlaceTo get(String id, User user) {
        LunchPlace place = ofNullable(lunchPlaceRepository.getWithPhones(id, user.getId()))
                .orElseThrow(() -> new NotFoundException(id, LunchPlace.class));
        return LunchPlaceTransformer.toDto(place);
    }

    @Override
    public Collection<LunchPlaceTo> getAll(User user) {
        Collection<LunchPlace> places = lunchPlaceRepository.getAll(user.getId());
        return LunchPlaceTransformer.collectionToDto(places);
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
    public MenuTo addMenu(String lunchPlaceId, MenuTo menuTo, User user) {
        LunchPlace place = ofNullable(lunchPlaceRepository.getWithPhones(lunchPlaceId, user.getId()))
                                .orElseThrow(() -> new NotFoundException(lunchPlaceId, LunchPlace.class));
        Menu created = place.createMenu(menuTo.getEffectiveDate(), menuTo.getDishes());
        menuRepository.save(created);
        return new MenuTo(created.getId(), created.getEffectiveDate(), menuTo.getDishes(), place.getId());
    }

    @Override
    @Transactional
    public void deleteMenu(String lunchPlaceId, String menuId, User user) {
        LunchPlace place = ofNullable(lunchPlaceRepository.get(lunchPlaceId, user.getId()))
                .orElseThrow(() -> new NotFoundException(lunchPlaceId, LunchPlace.class));
        menuRepository.deleteMenu(place.getId(), menuId);
    }
}
