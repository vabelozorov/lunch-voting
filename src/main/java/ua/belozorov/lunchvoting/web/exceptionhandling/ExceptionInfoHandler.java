package ua.belozorov.lunchvoting.web.exceptionhandling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ua.belozorov.lunchvoting.exceptions.DuplicateDataException;
import ua.belozorov.lunchvoting.exceptions.MultipleAreaMembershipForbiddenException;
import ua.belozorov.lunchvoting.exceptions.NotFoundException;

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

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DuplicateDataException.class)
    @ResponseBody
    public ErrorInfo handleConflict(HttpServletRequest req, DuplicateDataException ex) {
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

    @ResponseStatus(HttpStatus.NOT_FOUND)  //404
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public ErrorInfo handleEntityNotFound(HttpServletRequest req, NotFoundException ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  //404
    @ExceptionHandler(MultipleAreaMembershipForbiddenException.class)
    @ResponseBody
    public ErrorInfo handleEntityNotFound(HttpServletRequest req, MultipleAreaMembershipForbiddenException ex) {
        return new ErrorInfo(req.getRequestURL(), ex);
    }
}
