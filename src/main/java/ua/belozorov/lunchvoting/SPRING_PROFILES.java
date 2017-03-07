package ua.belozorov.lunchvoting;

import ua.belozorov.lunchvoting.config.datasource.HsqlDataSourceConfig;
import ua.belozorov.lunchvoting.config.datasource.PostgresDataSourceConfig;

/**
 * Class-container for the constants that name Spring profiles
 *
 * Created on 02.12.16.
 */
public final class SPRING_PROFILES {

    private SPRING_PROFILES() { }

    /**
     * Enables output of actual SQL queries (with values).
     * Requires P6Spy and datasource-proxy dependencies.
     * Must be enabled for tests to assert SQL query counts
     */
    public static final String SQL_DEBUG = "SQL_DEBUG";

    /**
     * Switches between databases. For configuration see {@link HsqlDataSourceConfig}
     * and {@link PostgresDataSourceConfig}
     * Only one of them should be used, obviously.
     */
    public static class DB {
        public static final String POSTGRES = "POSTGRES";
        public static final String HSQL = "HSQL";
    }
}
