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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    public Collection<LunchPlace> getMultipleWithMenu(Collection<String> placeIds, LocalDate startDate, LocalDate endDate, User user) {
        return this.checkAllPresent(placeIds, lunchPlaceRepository.getWithMenu(placeIds, startDate, endDate));
    }

    @Override
    public Collection<LunchPlace> getAll(User user) {
        return lunchPlaceRepository.getAll(user.getId());
    }

    @Override
    public Collection<LunchPlace> getMultiple(Collection<String> placeIds, User user) {
        if (placeIds.isEmpty()) {
            return lunchPlaceRepository.getAll(user.getId());
        } else {
            Collection<LunchPlace> places = lunchPlaceRepository.getMultiple(placeIds);
            // if not all LP entities got found...
            return this.checkAllPresent(placeIds, places);
        }
    }

    private Collection<LunchPlace> checkAllPresent(Collection<String> placeIds, Collection<LunchPlace> result) {
        if (result.size() != placeIds.size()) {
            ArrayList<String> ids = new ArrayList<>(placeIds);
            result.forEach(place -> ids.remove(place.getId()));
            throw new NotFoundException(ids, LunchPlace.class);
        }
        return result;
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
