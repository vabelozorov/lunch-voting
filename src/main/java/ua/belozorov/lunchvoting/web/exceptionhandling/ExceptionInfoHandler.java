package ua.belozorov.lunchvoting.web.exceptionhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.*;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
@ControllerAdvice(annotations = RestController.class)
public final class ExceptionInfoHandler {
    private static final String ERROR_MESSAGE_TEMPLATE = "field '%s', rejected value '%s', reason: %s";
    private final Logger LOG = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private final MessageSource messageSource;
    private final ErrorInfoFactory errorInfoFactory;

    @Autowired
    public ExceptionInfoHandler(MessageSource messageSource, ErrorInfoFactory errorInfoFactory) {
        this.messageSource = messageSource;
        this.errorInfoFactory = errorInfoFactory;
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DuplicateDataException.class)
    @ResponseBody
    public ErrorInfo handleConflict(HttpServletRequest req, DuplicateDataException ex) {
        return errorInfoFactory.create(req, ex);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleRequestParamsValidationFailure(HttpServletRequest req, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format(ERROR_MESSAGE_TEMPLATE,
                        fe.getField(),
                        fe.getRejectedValue(),
                        this.composeMessage(fe))
                )
                .collect(Collectors.joining("\n"));
        return new ErrorInfo(req.getRequestURL(), ErrorCode.PARAMS_VALIDATION_FAILED, msg);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInfo handleEntityNotFound(HttpServletRequest req, NotFoundException ex) {
        return errorInfoFactory.create(req, ex);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  //422
    @ExceptionHandler(NoAreaAdminException.class)
    @ResponseBody
    public ErrorInfo handleEntityNotFound(HttpServletRequest req, NoAreaAdminException ex) {
        return errorInfoFactory.create(req, ex);
    }

    private String composeMessage(FieldError fe) {
        String code = fe.getCode();
        if (code.startsWith("error.")) {
            return messageSource.getMessage(code, fe.getArguments(), LocaleContextHolder.getLocale());
        } else {
            return fe.getDefaultMessage();
        }
    }
}
