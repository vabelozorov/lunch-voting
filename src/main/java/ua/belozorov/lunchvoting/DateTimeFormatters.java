package ua.belozorov.lunchvoting;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Provides a single date and datetime format for the whole application
 *
 * Created on 29.12.16.
 */
public class DateTimeFormatters {
    public static final DateTimeFormatter DATE_FORMATTER =  DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DATE_TIME_FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class LocalDateFormatter implements Formatter<LocalDate> {

        @Override
        public LocalDate parse(String text, Locale locale) throws ParseException {
            return LocalDate.parse(text, DATE_FORMATTER);
        }

        @Override
        public String print(LocalDate lt, Locale locale) {
            return lt.format(DATE_FORMATTER);
        }
    }

    public static class LocalDateTimeFormatter implements Formatter<LocalDateTime> {

        @Override
        public LocalDateTime parse(String text, Locale locale) throws ParseException {
            return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        }

        @Override
        public String print(LocalDateTime ldt, Locale locale) {
            return ldt.format(DATE_TIME_FORMATTER);
        }
    }
}
