package ua.belozorov.lunchvoting.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vabelozorov on 14.11.16.
 */
@RestController
@RequestMapping("/api")
public class RootController {
    static final String HELLO_MESSAGE = "Hello from Lunch Voting system";

    @GetMapping("/hello")
    public String hello() {
        return HELLO_MESSAGE;
    }
}
