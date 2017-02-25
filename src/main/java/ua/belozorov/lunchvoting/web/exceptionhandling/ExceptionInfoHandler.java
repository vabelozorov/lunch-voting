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
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * Created on 07.02.17.
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
    public ErrorInfo handleFor409(HttpServletRequest req, ApplicationException ex) {
        return errorInfoFactory.create(req, ex);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleValidationFailure(HttpServletRequest req, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        String msg = result.getFieldErrors().stream()
                .map(fe -> String.format(ERROR_MESSAGE_TEMPLATE,
                        fe.getField(),
                        fe.getRejectedValue(),
                        this.composeMessage(fe))
                )
                .collect(Collectors.joining("\n"));
        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorCode.PARAMS_VALIDATION_FAILED, msg);
        return this.logAndCreate(errorInfo);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ErrorInfo handleValidationFailure(HttpServletRequest req, ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(cv -> String.format(ERROR_MESSAGE_TEMPLATE,
                        "unknown", //TODO fix error message for Spring method parameter validation
                        cv.getInvalidValue(),
                        cv.getMessage())
                ).collect(Collectors.joining("\n"));

        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorCode.PARAMS_VALIDATION_FAILED, msg);
        return this.logAndCreate(errorInfo);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInfo handleFor404(HttpServletRequest req, ApplicationException ex) {
        return this.logAndCreate(req, ex);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  //422
    @ExceptionHandler({NoAreaAdminException.class, PollException.class, VotePolicyException.class})
    @ResponseBody
    public ErrorInfo handleFor422(HttpServletRequest req, ApplicationException ex) {
        return this.logAndCreate(req, ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  //500
    @ExceptionHandler({IllegalStateException.class})
    public void handleApplicationUnexpectedState(HttpServletRequest req, IllegalStateException ex) throws Exception {
        int lineNumber = ex.getStackTrace()[0].getLineNumber();
        String className = ex.getStackTrace()[0].getClassName();
        String methodName = ex.getStackTrace()[0].getMethodName();
        String message = ex.getMessage();
        LOG.error(String.format("%s %s:%d %s", className, message, lineNumber, message));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  //500
    @ExceptionHandler(Exception.class)
    public void handleAnother(HttpServletRequest req, Exception ex) throws Exception {
//        LOG.error(ex.toString());
        throw ex;
    }


    private ErrorInfo logAndCreate(HttpServletRequest req, ApplicationException ex) {
        ErrorInfo errorInfo = errorInfoFactory.create(req, ex);
        LOG.debug(errorInfo.toString());
        return errorInfo;
    }

    private ErrorInfo logAndCreate(ErrorInfo errorInfo) {
        LOG.debug(errorInfo.toString());
        return errorInfo;
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
