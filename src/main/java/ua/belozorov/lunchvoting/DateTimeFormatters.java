package ua.belozorov.lunchvoting;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public static final DateTimeFormatter WEB_DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public static class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

        @Override
        public LocalDateTime parse(String text, Locale locale) throws ParseException {
            return LocalDateTime.parse(text, WEB_DATE_TIME_FORMATTER);
        }

        @Override
        public String print(LocalDateTime ldt, Locale locale) {
            return ldt.format(WEB_DATE_TIME_FORMATTER);
        }
    }
}
