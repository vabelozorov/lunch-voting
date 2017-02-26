package ua.belozorov.lunchvoting.web;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ua.belozorov.lunchvoting.model.lunchplace.Dish;
import ua.belozorov.lunchvoting.to.MenuTo;
import ua.belozorov.lunchvoting.web.AbstractControllerTest;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorCode;
import ua.belozorov.lunchvoting.web.exceptionhandling.ErrorInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created on 26.02.17.
 */
public class MenuControllerValidationTest extends AbstractControllerTest {
    public static final String REST_URL = MenuController.REST_URL;

    private final String areaId = testAreas.getFirstAreaId();

    @Test
    public void createMenuWithWrongDishFails() throws Exception {
        Object sent = this.createInvalidMenuToObject();

        MvcResult result = mockMvc
                .perform(
                        post(REST_URL, "any", "any")
                                .content( jsonUtils.toJson(sent))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .with(god())
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorInfo errorInfo = new ErrorInfo(
                result.getRequest().getRequestURL(),
                ErrorCode.PARAMS_VALIDATION_FAILED,
                "field 'effectiveDate', rejected value 'null', reason: may not be null\n" +
                        "field 'dishes[0].name', rejected value '', reason: name length must be between 2 and 50 included\n" +
                        "field 'dishes[0].price', rejected value '-1.0', reason: must be provided and greater or equal 0\n" +
                        "field 'dishes[1].position', rejected value '-1', reason: must be provided and greater or equal 0"
        );

        assertJson(
                jsonUtils.toJson(errorInfo),
                result.getResponse().getContentAsString()
        );
    }

    private Object createInvalidMenuToObject() {
        return new Object() {
            private List<Object> dishes = Arrays.asList(
                    new Object() {
                        private int position = 1;
                    },
                    new Object() {
                        private String name = "Second Dish";
                        private float price = 10.12f;
                    }
            );
        };
    }
}
