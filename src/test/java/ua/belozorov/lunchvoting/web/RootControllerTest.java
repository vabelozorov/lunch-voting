package ua.belozorov.lunchvoting.web;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Created by vabelozorov on 15.11.16.
 */
public class RootControllerTest extends AbstractControllerTest{

    @Test
    public void hello() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/hello"))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Assert.assertTrue(response.equals(RootController.HELLO_MESSAGE));
    }
}