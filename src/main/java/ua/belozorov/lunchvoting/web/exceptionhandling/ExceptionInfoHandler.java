package ua.belozorov.lunchvoting.web.exceptionhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.ApplicationException;
import ua.belozorov.lunchvoting.exceptions.DuplicateEmailException;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 07.02.17.
 */
@ControllerAdvice(annotations = RestController.class)
public final class ExceptionInfoHandler {
    Logger LOG = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseBody
    public ErrorInfo handleConflict(HttpServletRequest req, DuplicateEmailException ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo handleRequestParamsValidationFailure(HttpServletRequest req, MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        String msg = result.getFieldErrors().stream()
                .map(fe -> fe.getField() + ' ' + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return new ErrorInfo(req.getRequestURL(), Code.PARAMS_VALIDATION_FAILED, msg);
    }
}
