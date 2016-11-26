package ua.belozorov.lunchvoting.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping(LunchPlaceController.REST_URL)
public class MenuController {
    static final String REST_URL = "/place";

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
