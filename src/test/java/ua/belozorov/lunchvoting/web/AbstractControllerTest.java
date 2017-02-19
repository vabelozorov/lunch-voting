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
    private Filter springSecurityFilterChain;

    @Autowired
    public void setMockMvc(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    void assertJson(String expected, String actual) throws JSONException {
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.NON_EXTENSIBLE);
    }

    String getCreatedId(String uri) {
        return Arrays.stream(uri.split("/")).reduce((a, b) -> b).orElse(null);
    }
}
