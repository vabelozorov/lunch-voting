package ua.belozorov.lunchvoting.web;

import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import ua.belozorov.lunchvoting.AbstractSpringTest;
import ua.belozorov.lunchvoting.misc.JsonUtils;
import ua.belozorov.lunchvoting.TestConfig;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.config.WebConfig;
import ua.belozorov.lunchvoting.config.WebSecurityConfig;
import ua.belozorov.lunchvoting.TestServiceBeansConfig;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static ua.belozorov.lunchvoting.model.UserTestData.*;

/**
 * Created by vabelozorov on 15.11.16.
 */
@ContextHierarchy({
        @ContextConfiguration(classes = {RootConfig.class, TestServiceBeansConfig.class, WebSecurityConfig.class}),
        @ContextConfiguration(classes = {
                WebConfig.class, TestConfig.class
        })
})
@WebAppConfiguration
public abstract class AbstractControllerTest extends AbstractSpringTest {

//    private static final CharacterEncodingFilter CHARACTER_ENCODING_FILTER = new CharacterEncodingFilter();
//
//    static {
//        CHARACTER_ENCODING_FILTER.setEncoding("UTF-8");
//        CHARACTER_ENCODING_FILTER.setForceEncoding(true);
//    }

    @Autowired
    JsonUtils jsonUtils;

    MockMvc mockMvc;

    @Autowired
    public void setMockMvc(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    void assertJson(String expected, String actual) throws JSONException {
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }

    String getCreatedId(String uri) {
        return Arrays.stream(uri.split("/")).reduce((a, b) -> b).orElse(null);
    }

    static RequestPostProcessor god() {
        return user(GOD);
    }

    static RequestPostProcessor voter() {
        return user(VOTER);
    }

    static RequestPostProcessor alien() {
        return user(ALIEN_USER1);
    }
}
