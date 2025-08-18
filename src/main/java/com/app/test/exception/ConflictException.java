package com.app.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.app.test.exception.model.Error;

import java.util.List;


@ResponseStatus(code = HttpStatus.CONFLICT, value = HttpStatus.CONFLICT)
public class ConflictException extends BaseException {

    public ConflictException() {
    }

    public ConflictException(Error error) {
        super.addError(error);
    }

    public ConflictException(List<Error> errors) {
        super.getErrors().addAll(errors);
    }
}
