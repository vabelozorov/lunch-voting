package ua.belozorov.lunchvoting.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ua.belozorov.lunchvoting.AbstractSpringTest;
import ua.belozorov.lunchvoting.AbstractTest;
import ua.belozorov.lunchvoting.JsonUtils;
import ua.belozorov.lunchvoting.TestConfig;
import ua.belozorov.lunchvoting.config.RootConfig;
import ua.belozorov.lunchvoting.config.WebConfig;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static ua.belozorov.lunchvoting.model.UserTestData.ALIEN_USER1;
import static ua.belozorov.lunchvoting.model.UserTestData.GOD;
import static ua.belozorov.lunchvoting.model.UserTestData.VOTER;

//import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Created by vabelozorov on 15.11.16.
 */
@ContextConfiguration(classes = {RootConfig.class, WebConfig.class, TestConfig.class})
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
        return httpBasic(GOD.getEmail(), GOD.getPassword());
    }

    static RequestPostProcessor voter() {
        return httpBasic(VOTER.getEmail(), VOTER.getPassword());
    }

    static RequestPostProcessor alien() {
        return httpBasic(ALIEN_USER1.getEmail(), ALIEN_USER1.getPassword());
    }
}
