package com.app.test.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import com.app.test.exception.BaseException;
import com.app.test.exception.ValidationException;
import com.app.test.exception.model.Error;
import com.app.test.exception.model.ErrorResponse;
import com.app.test.exception.model.InternalErrorCode;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler {

    private static final String ERROR_MESSAGE_TEMPLATE = "%n requested uri: %s;%n http status: %s;%n http error: %s;%n messages: %n %s.";
    private static final String LIST_JOIN_DELIMITER = "\n";

    /**
     * handle the exception throw when an argument annotated with @Valid does not respect the constraints
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> methodArgumentNotValidHandler(MethodArgumentNotValidException ex, WebRequest request) {

        List<Error> collect = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                            StringBuilder builder = new StringBuilder()
                                    .append("'")
                                    .append(error.getField())
                                    .append("' ")
                                    .append(error.getDefaultMessage());
                            //Nice to have. Mappare l'errore in modo da impostare un internal error più specifico (es: required, not blanck, max_lenght ecc)
                            return new Error(InternalErrorCode.PARAMETER_INVALID, builder.toString());
                        }
                )
                .collect(Collectors.toList());

        ValidationException validationException = new ValidationException(collect);
        validationException.setStackTrace(ex.getStackTrace());

        return generalExceptionHandler(validationException, request);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    private ResponseEntity<ErrorResponse> dataIntegrityViolationHandler(DataIntegrityViolationException ex, WebRequest request) {
        log.debug("---DEBUG CALLING METHOD: dataIntegrityViolationHandler()");

        ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex.getCause();

        Error error = new Error(InternalErrorCode.CONFLICT, constraintViolationException.getSQLException().getMessage(), constraintViolationException.getSQL());
        BaseException baseException = new BaseException();
        baseException.addError(error);
        baseException.setStackTrace(ex.getStackTrace());

        ErrorResponse errorResponse = getErrorResponse(baseException, HttpStatus.CONFLICT, request);

        logError(baseException, errorResponse, request);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));

    }

    /**
     * handle the wrong permissions exception
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> authorizationDeniedHandler(AuthorizationDeniedException ex, WebRequest request) {
        log.debug("---DEBUG CALLING METHOD: accessDeniedHandler()");

        Error error = new Error(InternalErrorCode.FORBIDDEN, "Access denied: insufficient permissions.", ex.getMessage());
        BaseException baseException = new BaseException();
        baseException.addError(error);
        baseException.setStackTrace(ex.getStackTrace());

        ErrorResponse errorResponse = getErrorResponse(baseException, HttpStatus.FORBIDDEN, request);

        logError(baseException, errorResponse, request);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

    /**
     * general handler for all exceptions
     */
    @ExceptionHandler({Exception.class})
    private ResponseEntity<ErrorResponse> generalExceptionHandler(Exception exception, WebRequest request) {
        log.debug("---DEBUG CALLING METHOD: generalExceptionHandler()");

        BaseException baseException = new BaseException();

        if (exception instanceof BaseException) {
            baseException = (BaseException) exception;
        } else {
            Error error = new Error(InternalErrorCode.UNHANDLED_ERROR, exception.getMessage());
            baseException.addError(error);
        }
        baseException.setStackTrace(exception.getStackTrace());

        Class<? extends Exception> exceptionClass = exception.getClass();
        ResponseStatus responseStatus = exceptionClass.getAnnotation(ResponseStatus.class);
        final HttpStatus status = responseStatus != null ? responseStatus.code() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = getErrorResponse(baseException, status, request);

        logError(baseException, errorResponse, null);

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

    /**
     * @param baseException
     * @param httpStatus
     * @param request
     * @return
     */
    public static ErrorResponse getErrorResponse(BaseException baseException, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setPath(request.getDescription(false));
        errorResponse.setTimestamp(Instant.now());

        errorResponse.setStatus(httpStatus.value());
        errorResponse.setError(httpStatus.getReasonPhrase());

        errorResponse.setErrors(baseException.getErrors());
        return errorResponse;
    }

    private void logError(BaseException baseException, ErrorResponse response, @Nullable WebRequest request) {
        log.error(buildErrorMessageTemplate(response));
        if (response.getStatus() == 500) //error not handled
            log.error(Arrays.stream(baseException.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
    }

    private String buildErrorMessageTemplate(ErrorResponse response) {
        return buildErrorMessageTemplate(response.getPath(), response.getStatus(), response.getError(), response.getErrors());
    }

    private String buildErrorMessageTemplate(String requestedUri, Integer httpStatus, String httpError, List<Error> errors) {
        String messages = "";

        if (errors != null && !errors.isEmpty()) {
            messages = messages.concat(
                    errors
                            .stream()
                            .map(e -> {
                                return "Error code: " + e.getErrorCode() + " - Error Message: " + e.getErrorMessage() + " - Dev message: " + e.getDevMessage();
                            })
                            .collect(Collectors.joining(LIST_JOIN_DELIMITER))
            );
        }
        return new StringFormattedMessage(ERROR_MESSAGE_TEMPLATE, requestedUri, httpStatus, httpError, messages).toString();
    }



}
