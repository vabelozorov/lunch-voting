package ua.belozorov.lunchvoting;

/**

 *
 * Created on 02.12.16.
 */
public final class SPRING_PROFILES {

    private SPRING_PROFILES() { }

    public static final String SQL_DEBUG = "SQL_DEBUG";

    public static class DB {
        public static final String POSTGRES = "POSTGRES";
        public static final String HSQL = "HSQL";
    }
}
