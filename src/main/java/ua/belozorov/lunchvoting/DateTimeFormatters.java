package ua.belozorov.lunchvoting;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 29.12.16.
 */
public class DateTimeFormatters {
    public static final DateTimeFormatter WEB_DATE_FORMATTER =  DateTimeFormatter.ISO_LOCAL_DATE;

    public static class LocalDateFormatter implements Formatter<LocalDate> {

        @Override
        public LocalDate parse(String text, Locale locale) throws ParseException {
            return LocalDate.parse(text, WEB_DATE_FORMATTER);
        }

        @Override
        public String print(LocalDate lt, Locale locale) {
            return lt.format(WEB_DATE_FORMATTER);
        }
    }
//
//    public static class LocalTimeFormatter implements Formatter<LocalTime> {
//
//        @Override
//        public LocalTime parse(String text, Locale locale) throws ParseException {
//            return TimeUtil.parseLocalTime(text);
//        }
//
//        @Override
//        public String print(LocalTime lt, Locale locale) {
//            return lt.format(DateTimeFormatter.ISO_LOCAL_TIME);
//        }
//    }
}
