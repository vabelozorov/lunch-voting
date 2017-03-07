package ua.belozorov.lunchvoting.web.exceptionhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created on 04.03.17.
 */
public class AppHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    private final static Logger logger = LoggerFactory.getLogger(AppHandlerExceptionResolver.class);
    private final ObjectMapper objectMapper;

    public AppHandlerExceptionResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        logger.debug("Resolving " + ex.toString());
        if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            ErrorInfo errorInfo = new ErrorInfo(request.getRequestURL(), ErrorCode.AUTH_NO_PERMISSIONS);
            this.printToResponse(response, errorInfo);
            return new ModelAndView();
        }
        return super.doResolveException(request, response, handler, ex);
    }

    @Override
    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        String[] supportedMethods = ex.getSupportedMethods();
        if (supportedMethods != null) {
            response.setHeader("Allow", StringUtils.arrayToDelimitedString(supportedMethods, ", "));
        }
        ErrorInfo errorInfo = new ErrorInfo(request.getRequestURL(), ErrorCode.METHOD_NOT_ALLOWED, ex.getMethod());
        response.getWriter().print(objectMapper.writeValueAsString(errorInfo));
        return new ModelAndView();
    }

    @Override
    protected ModelAndView handleMissingPathVariable(MissingPathVariableException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        this.notFound(request, response);
        return new ModelAndView();
    }

    @Override
    protected ModelAndView handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorInfo errorInfo = new ErrorInfo(request.getRequestURL(), ErrorCode.UNEXPECTED_CONTENT);
        response.getWriter().print(objectMapper.writeValueAsString(errorInfo));
        return new ModelAndView();
    }

    @Override
    protected ModelAndView handleBindException(BindException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        return super.handleBindException(ex, request, response, handler);
    }

    @Override
    protected ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        this.notFound(request, response);
        return new ModelAndView();
    }

    @Override
    protected ModelAndView handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        return super.handleAsyncRequestTimeoutException(ex, request, response, handler);
    }

    private void notFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        ErrorInfo errorInfo = new ErrorInfo(request.getRequestURL(), ErrorCode.URL_NOT_FOUND);
        response.getWriter().print(objectMapper.writeValueAsString(errorInfo));
    }

    private void printToResponse(HttpServletResponse response, ErrorInfo errorInfo) {
        try {
            response.getWriter().print(objectMapper.writeValueAsString(errorInfo));
        } catch (IOException e) {
            logger.warn("Failed to write error object to response.");
        }
    }
}
