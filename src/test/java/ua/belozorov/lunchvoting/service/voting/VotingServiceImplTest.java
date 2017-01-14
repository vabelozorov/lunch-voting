package ua.belozorov.lunchvoting.service.voting;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.belozorov.lunchvoting.model.base.AbstractPersistableObject;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.model.lunchplace.Menu;
import ua.belozorov.lunchvoting.model.voting.Poll;
import ua.belozorov.lunchvoting.model.voting.PollItem;
import ua.belozorov.lunchvoting.service.AbstractServiceTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static ua.belozorov.lunchvoting.model.lunchplace.LunchPlaceTestData.*;


/**
 * <h2></h2>
 *
 * @author vabelozorov on 04.01.17.
 */
public class VotingServiceImplTest extends AbstractServiceTest {

    @Autowired
    private VotingService votingService;

    @Test
    public void testCreatePollForTodayMenus() throws Exception {
        String pollId = votingService.createPollForTodayMenus();
        Poll poll = votingService.getPollFullDetails(pollId);

        Set<LunchPlace> places = poll.getPollItems().stream()
                .map(PollItem::getItem)
                .collect(Collectors.toSet());
        Set<LunchPlace> expectedPlaces = new HashSet<>(Arrays.asList(placeTestData.getPlace3(), placeTestData.getPlace4()));

        // PollItems contain expected LunchPlace IDs
        assertTrue(
               places.stream()
                    .map(LunchPlace::getId)
                    .collect(Collectors.toSet())
                    .equals(
                            expectedPlaces.stream()
                                    .map(LunchPlace::getId).collect(Collectors.toSet())
        ));

        // LunchPlaces contain expected menus (that are active today)
        assertTrue(
                places.stream()
                    .flatMap(place -> place.getMenus().stream())
                    .collect(Collectors.toSet())
                    .equals(
                            placeTestData.getMenus().stream()
                                    .filter(menu -> menu.getEffectiveDate().equals(LocalDate.now())
                                                    && expectedPlaces.contains(menu.getLunchPlace()))
                                    .collect(Collectors.toSet())
        ));
    }

    @Test
    public void testVote() throws Exception {

    }

    @Test
    public void testGetPollItemDetails() throws Exception {

    }

    @Test
    public void testGetPollItemDetails1() throws Exception {

    }

}