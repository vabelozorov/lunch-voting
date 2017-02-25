package ua.belozorov.lunchvoting.web.exceptionhandling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import ua.belozorov.lunchvoting.exceptions.ApplicationException;

import javax.servlet.http.HttpServletRequest;

/**
 * <h2></h2>
 *
 * Created on 17.02.17.
 */
public class ErrorInfoFactory {

    private final MessageSource messageSource;

    @Autowired
    public ErrorInfoFactory(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    ErrorInfo create(HttpServletRequest request, ApplicationException ex) {
        return new ErrorInfo(request.getRequestURL(), ex.getErrorCode(), this.composeMessage(ex));
    }

    ErrorInfo create(HttpServletRequest request, ErrorCode errorCode, String message) {
        return new ErrorInfo(request.getRequestURL() , errorCode, message);
    }

    private String composeMessage(ApplicationException ex) {
        return messageSource.getMessage(ex.getStringErrorCode(), ex.getArgs(), LocaleContextHolder.getLocale());
    }
}
