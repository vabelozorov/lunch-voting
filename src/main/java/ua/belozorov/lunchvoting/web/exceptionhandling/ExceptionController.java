package ua.belozorov.lunchvoting.web.exceptionhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ua.belozorov.lunchvoting.web.exceptionhandling.exceptions.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Strategy is the following:
 * <ul>
 *     <li>Application-specific exception implements {@link ApplicationException}. This interface provides
 *     a consistent way to build a meaningful response to a client error through {@link ErrorInfo} /li>
 *     <li>A number of Spring-specific exceptions, e.g. validation-related ones, are processed in a way which
 *     adapts them to {@link ErrorInfo}</li>
 *     <li>Other Spring MVC exceptions are handled via {@link ExceptionHandler} to prevent Tomcat from generating
 *     an HTML error page, but retain original response codes. </li>
 *     <li>All other exceptions are logged and a client are sent a status code 500</li>
 * </ul>
 *
 * Created on 07.02.17.
 */
@ControllerAdvice(annotations = RestController.class)
public final class ExceptionController {
    private final Logger LOG = LoggerFactory.getLogger(ExceptionController.class);

    private static final String ERROR_MESSAGE_TEMPLATE = "field '%s', rejected value '%s', reason: %s";

    private final MessageSource messageSource;

    private final ErrorInfoFactory errorInfoFactory;

    @Autowired
    public ExceptionController(MessageSource messageSource, ErrorInfoFactory errorInfoFactory) {
        this.messageSource = messageSource;
        this.errorInfoFactory = errorInfoFactory;
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
                        this.composeMessageFromMessageSource(fe))
                )
                .collect(Collectors.joining("\n"));
        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorCode.PARAMS_VALIDATION_FAILED, msg);
        return this.logAndCreate(errorInfo);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public ErrorInfo handleValidationFailure(HttpServletRequest req, ConstraintViolationException ex) {
        String msg = ex.getConstraintViolations().stream()
                .map(cv -> String.format(ERROR_MESSAGE_TEMPLATE,
                        "unknown", //TODO fix error message for Spring method parameter validation
                        cv.getInvalidValue(),
                        cv.getMessage())
                )
                .collect(Collectors.joining("\n"));
        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorCode.PARAMS_VALIDATION_FAILED, msg);
        return this.logAndCreate(errorInfo);
    }

    //case when no credentials were submitted or no user's areaId when it's required by a calling method
    @ResponseStatus(HttpStatus.UNAUTHORIZED)  //401
    @ExceptionHandler({CannotIdentifyException.class})
    @ResponseBody
    public ErrorInfo handle401NoUserIdInfo(HttpServletRequest req, CannotIdentifyException ex) {
        return this.logAndCreate(req, ex);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)  //403
    @ExceptionHandler({AccessDeniedException.class})
    @ResponseBody
    public ErrorInfo handleAccessDenied(HttpServletRequest req, AccessDeniedException ex) {
        ErrorInfo errorInfo = new ErrorInfo(req.getRequestURL(), ErrorCode.AUTH_NO_PERMISSIONS);
        return this.logAndCreate(errorInfo);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInfo handleFor404(HttpServletRequest req, ApplicationException ex) {
        return this.logAndCreate(req, ex);
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DuplicateDataException.class)
    @ResponseBody
    public ErrorInfo handleFor409(HttpServletRequest req, DuplicateDataException ex) {
        return this.logAndCreate(req, ex);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  //422
    @ExceptionHandler({NoAreaAdminException.class, PollException.class, VotePolicyException.class,
            JoinRequestException.class})
    @ResponseBody
    public ErrorInfo handleFor422(HttpServletRequest req, ApplicationException ex) {
        return this.logAndCreate(req, ex);
    }

    /**
     * Hanldles other <code>application</code> exceptions
     * @param req
     * @param ex
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  //500
    @ExceptionHandler(Exception.class)
    public void handleOther(HttpServletRequest req, Exception ex) throws Exception {
        this.log(req, ex);
    }

    private ErrorInfo logAndCreate(HttpServletRequest req, ApplicationException ex) {
        ErrorInfo errorInfo = errorInfoFactory.create(req, ex);
        return this.logAndCreate(errorInfo);
    }

    private ErrorInfo logAndCreate(ErrorInfo errorInfo) {
        LOG.debug(errorInfo.toString());
        return errorInfo;
    }

    private void log(HttpServletRequest req, Exception ex) {
        LOG.debug(this.composeGeneralExceptionMessage(req, ex));
    }

    private String composeGeneralExceptionMessage(HttpServletRequest req, Exception ex) {
        int lineNumber = ex.getStackTrace()[0].getLineNumber();
        String className = ex.getStackTrace()[0].getClassName();
        String methodName = ex.getStackTrace()[0].getMethodName();
        String message = ex.getMessage();
        return String.format("Request %s caused: %s %s:%d %s", req.getRequestURL(),
                className, methodName, lineNumber, message);
    }

    private String composeMessageFromMessageSource(FieldError fe) {
        String code = fe.getCode();
        if (code.startsWith("error.")) {
            String message = messageSource.getMessage(code, fe.getArguments(), code, LocaleContextHolder.getLocale());
            if (message.equals(code)) {
                LOG.warn("no message for error code " + code);
            }
            return message;
        } else {
            return fe.getDefaultMessage();
        }
    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
}
