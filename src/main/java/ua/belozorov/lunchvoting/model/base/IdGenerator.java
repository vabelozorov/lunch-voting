package ua.belozorov.lunchvoting.model.base;

import java.util.UUID;

/**
 * Created by vabelozorov on 09.10.16.
 */
public class IdGenerator {

    private IdGenerator() {
    }

    public static String createId() {
        return UUID.randomUUID().toString();
    }

}
