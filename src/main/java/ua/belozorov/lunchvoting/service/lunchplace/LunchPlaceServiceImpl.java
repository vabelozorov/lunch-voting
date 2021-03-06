package ua.belozorov.lunchvoting.service.lunchplace;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.NotFoundException;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.repository.lunchplace.LunchPlaceRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepository;
import ua.belozorov.lunchvoting.repository.lunchplace.MenuRepositoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;

/**

 *
 * Created on 21.11.16.
 */
@Service
@Transactional(readOnly = true)
public class LunchPlaceServiceImpl implements LunchPlaceService {

    private final LunchPlaceRepository lunchPlaceRepository;

    private final MenuRepository menuRepository;

    @Autowired
    public LunchPlaceServiceImpl(LunchPlaceRepository lunchPlaceRepository, MenuRepository menuRepository) {
        this.lunchPlaceRepository = lunchPlaceRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    @Transactional
    public LunchPlace create(LunchPlace place) {
        return lunchPlaceRepository.save(place);
    }

    @Override
    @Transactional
    public void bulkUpdate(String areaId, String placeId, @Nullable String name, @Nullable String address,
                           @Nullable String description, @Nullable Set<String> phones) {
        LunchPlace toBeUpdated = this.get(areaId, placeId);

        if (name != null) {
            toBeUpdated = toBeUpdated.withName(name);
        }
        if(address != null) {
            toBeUpdated = toBeUpdated.withAddress(address);
        }
        if(description != null) {
            toBeUpdated = toBeUpdated.withDescription(description);
        }
        if (phones != null) {
            toBeUpdated = toBeUpdated.withPhones(phones);
        }
        lunchPlaceRepository.update(toBeUpdated);
    }

    @Override
    public LunchPlace get(String areaId, String placeId) {
        return ofNullable(lunchPlaceRepository.get(areaId, placeId))
                .orElseThrow(() -> new NotFoundException(placeId, LunchPlace.class));
    }

    @Override
    public List<LunchPlace> getMultipleWithMenu(String areaId, Set<String> placeIds, LocalDate startDate, LocalDate endDate) {
        List<LunchPlace> places = lunchPlaceRepository.getWithMenu(areaId, placeIds, startDate, endDate);
        return this.checkAllPresent(placeIds, places);
    }

    @Override
    public List<LunchPlace> getAll(String areaId) {
        return lunchPlaceRepository.getAll(areaId);
    }

    @Override
    public List<LunchPlace> getMultiple(String areaId, Set<String> placeIds) {
        if (placeIds.isEmpty()) {
            return lunchPlaceRepository.getAll(areaId);
        } else {
            List<LunchPlace> places = lunchPlaceRepository.getMultiple(areaId, placeIds);
            // if not all LP entities got found...
            return this.checkAllPresent(placeIds, places);
        }
    }

    private List<LunchPlace> checkAllPresent(Set<String> placeIds, List<LunchPlace> result) {
        if (result.size() != placeIds.size()) {
            List<String> ids = new ArrayList<>(placeIds);
            result.forEach(place -> ids.remove(place.getId()));
            throw new NotFoundException(ids, LunchPlace.class);
        }
        return result;
    }

    @Override
    @Transactional
    public void delete(String areaId, String id) {
        if ( ! lunchPlaceRepository.delete(areaId, id)) {
            throw new NotFoundException(id, LunchPlace.class);
        }
    }

    @Override
    @Transactional
    public Menu addMenu(String areaId, String lunchPlaceId, LocalDate effectiveDate, Set<Dish> dishes) {
        LunchPlace place = this.get(areaId, lunchPlaceId);
        Menu created = new Menu(effectiveDate, dishes, place);
        return menuRepository.save(created);
    }

    @Override
    @Transactional
    public void deleteMenu(String areaId, String lunchPlaceId, String menuId) {
        this.getMenu(areaId, lunchPlaceId, menuId);
        menuRepository.delete(menuId);
    }

    @Override
    public Menu getMenu(String areaId, String placeId, String menuId, MenuRepositoryImpl.Fields... fields) {
        return ofNullable(menuRepository.get(areaId, placeId, menuId, fields))
                .orElseThrow(() -> new NotFoundException(menuId, Menu.class));
    }

    @Override
    public LunchPlaceRepository getRepository() {
        return this.lunchPlaceRepository;
    }
}
