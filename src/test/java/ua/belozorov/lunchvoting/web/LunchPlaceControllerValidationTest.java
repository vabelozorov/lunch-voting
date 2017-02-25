package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.lunchplace.LunchPlace;
import ua.belozorov.lunchvoting.to.LunchPlaceTo;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 * Created on 22.02.17.
 */
public class LunchPlaceControllerValidationTest extends AbstractControllerTest {
    private static final String REST_URL = LunchPlaceController.REST_URL;
    private final String areaId = testAreas.getFirstAreaId();
    private final LunchPlace PLACE4 = super.testPlaces.getPlace4();
    private final String PLACE4_ID = PLACE4.getId();

    @Test
    public void validateLunchPlaceTo() throws Exception {
        Set<String> phones = ImmutableSet.of("0661234567x", "04412345671");
        LunchPlaceTo to = new LunchPlaceTo("Updated PLace", null, "<script>alert()</script>", phones);

        MvcResult result = mockMvc
                .perform(
                        put(REST_URL + "/{id}", areaId, PLACE4_ID)
                        .content(jsonUtils.toJson(to))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(god())
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        ErrorInfo errorInfo = new ErrorInfo(result.getRequest().getRequestURL(),
                ErrorCode.PARAMS_VALIDATION_FAILED,
                "field 'description', rejected value '<script>alert()</script>', reason: may have unsafe html content\n" +
                        "field 'phones[].phone', rejected value '0661234567x', reason: error.model.lunchplace.phone_format_invalid\n" +
                        "field 'phones[].phone', rejected value '04412345671', reason: error.model.lunchplace.phone_format_invalid"
        );
//        assertJson(
//                jsonUtils.toJson(errorInfo),
//                result.getResponse().getContentAsString()
//        );
    }
}
